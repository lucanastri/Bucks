package com.kizune.bucks.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.kizune.bucks.ui.*
import com.kizune.bucks.ui.checking.CheckingDestination
import com.kizune.bucks.ui.checking.CheckingScreen
import com.kizune.bucks.ui.editfund.EditFundDestination
import com.kizune.bucks.ui.editfund.EditFundScreen
import com.kizune.bucks.ui.insertfund.InsertFundDestination
import com.kizune.bucks.ui.insertfund.InsertFundScreen
import com.kizune.bucks.ui.insertmovement.InsertMovementDestination
import com.kizune.bucks.ui.insertmovement.InsertMovementScreen
import com.kizune.bucks.utils.enterTransition
import com.kizune.bucks.utils.exitTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ParentNavHost(
    widthSize: WindowWidthSizeClass,
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
) {
    AnimatedNavHost(
        navController = navigationUIHandler.parentNavController,
        startDestination = navigationUIHandler.startDestination,
        enterTransition = { enterTransition() },
        exitTransition = { exitTransition() },
        popEnterTransition = { enterTransition() },
        popExitTransition = { exitTransition() },
        modifier = modifier
    ) {
        composable(route = DashboardDestination.route) {
            DashboardScreen(
                widthSize = widthSize,
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(route = InsertFundDestination.route) {
            InsertFundScreen(
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(
            route = EditFundDestination.routeWithArgs,
            arguments = listOf(navArgument(EditFundDestination.fundIDArg) {
                type = NavType.LongType
            })
        ) {
            EditFundScreen(
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(
            route = InsertMovementDestination.routeWithArgs,
            arguments = listOf(navArgument(InsertMovementDestination.fundIDArg)
            {
                type = NavType.LongType
            })
        ) {
            InsertMovementScreen(
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(
            route = CheckingDestination.routeWithArgs,
            arguments = listOf(navArgument(CheckingDestination.fundIDArg) {
                type = NavType.LongType
            })
        ) {
            CheckingScreen(
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
    }
}