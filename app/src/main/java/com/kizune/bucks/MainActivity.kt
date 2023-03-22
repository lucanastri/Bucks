package com.kizune.bucks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.BucksApplication

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("MyTag", "exception", throwable)
        }
        super.onCreate(savedInstanceState)

        setContent {
            BucksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val widthSize = calculateWindowSizeClass(this).widthSizeClass

                    val systemUiController = rememberSystemUiController()
                    systemUiController.setNavigationBarColor(
                        color = when (widthSize) {
                            WindowWidthSizeClass.Compact -> colorResource(id = R.color.surface2)
                            WindowWidthSizeClass.Medium -> colorResource(id = R.color.surface2)
                            WindowWidthSizeClass.Expanded -> colorResource(id = R.color.surface1)
                            else -> colorResource(id = R.color.surface2)
                        },
                        darkIcons = !isSystemInDarkTheme()
                    )

                    systemUiController.setStatusBarColor(
                        color = when (widthSize) {
                            WindowWidthSizeClass.Compact -> MaterialTheme.colorScheme.background
                            WindowWidthSizeClass.Medium -> colorResource(id = R.color.surface2)
                            WindowWidthSizeClass.Expanded -> colorResource(id = R.color.surface1)
                            else -> MaterialTheme.colorScheme.background
                        },
                        darkIcons = !isSystemInDarkTheme()
                    )
                    BucksApplication(
                        widthSize = widthSize,
                        moveTaskToBack = { moveTaskToBack(true) }
                    )
                }
            }
        }
    }
}
