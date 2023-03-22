package com.kizune.bucks.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.ui.navigation.ParentNavHost
import com.kizune.bucks.ui.settings.SettingsUiStateResult
import com.kizune.bucks.ui.settings.SettingsViewModel
import com.kizune.bucks.ui.slide.SlideScreen
import com.kizune.bucks.ui.slide.SlideUIHandler

/**
 * Entry point dell'applicazione che sceglie tra l'onBoardingScreen e l'AppScreen
 */
@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun BucksApplication(
    widthSize: WindowWidthSizeClass,
    moveTaskToBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState = viewModel.preferenceUiState.collectAsState().value

    when (uiState.result) {
        SettingsUiStateResult.Loading -> {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.background))
        }
        SettingsUiStateResult.Success -> {
            when (uiState.onBoardingPreference) {
                false -> {
                    val slideUIHandler = SlideUIHandler(
                        viewModel = viewModel,
                        pagerState = rememberPagerState(),
                        coroutineScope = rememberCoroutineScope(),
                        moveTaskToBack = moveTaskToBack,
                    )

                    SlideScreen(
                        widthSize = widthSize,
                        slideUIHandler = slideUIHandler
                    )
                }
                true -> {
                    val navigationUIHandler = NavigationUIHandler(
                        parentNavController = rememberAnimatedNavController(),
                        childNavController = rememberNavController(),
                        moveTaskToBack = moveTaskToBack,
                    )

                    ParentNavHost(
                        widthSize = widthSize,
                        navigationUIHandler = navigationUIHandler,
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                }
            }
        }
    }
}