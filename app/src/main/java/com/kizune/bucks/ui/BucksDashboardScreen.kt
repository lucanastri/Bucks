package com.kizune.bucks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.bottomsheet.BottomSheet
import com.kizune.bucks.ui.bottomsheet.BottomSheetType
import com.kizune.bucks.ui.navigation.*
import kotlinx.coroutines.launch

object DashboardDestination : Destination {
    override val route = "Dashboard"
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    widthSize: WindowWidthSizeClass,
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        } else {
            navigationUIHandler.moveTaskToBack()
        }
    }

    var bottomSheetType by rememberSaveable { mutableStateOf(BottomSheetType.CURRENCY) }

    val onSettingClicked: (BottomSheetType) -> Unit = {
        bottomSheetType = it
        coroutineScope.launch {
            bottomSheetState.show()
        }
    }

    ModalBottomSheetLayout(sheetContent = {
        BottomSheet(
            bottomSheetState = bottomSheetState,
            bottomSheetType = bottomSheetType
        )
    },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetElevation = 0.dp,
        sheetBackgroundColor = MaterialTheme.colorScheme.background,
        sheetContentColor = MaterialTheme.colorScheme.onBackground,
        content = {
            DashboardScaffold(
                widthSize = widthSize,
                onSettingClicked = onSettingClicked,
                navigationUIHandler = navigationUIHandler,
                bottomSheetState = bottomSheetState,
                modifier = modifier
            )
        })
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DashboardScaffold(
    widthSize: WindowWidthSizeClass,
    onSettingClicked: (BottomSheetType) -> Unit,
    navigationUIHandler: NavigationUIHandler,
    bottomSheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedItem by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                BucksSnackbar(
                    message = data.visuals.message,
                    type = (data.visuals as BucksSnackbarVisuals).type,
                    dismissAction = { snackbarHostState.currentSnackbarData?.dismiss() })
            }
        },
        bottomBar = {
            if (widthSize == WindowWidthSizeClass.Compact) {
                BottomBar(
                    selectedItem = selectedItem,
                    onNavigationItemClick = { index ->
                        selectedItem = index
                        navigationUIHandler.onNavigationItemClick(selectedItem)
                    }
                )
            }
        },
        content = { padding ->
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (widthSize) {
                    WindowWidthSizeClass.Medium -> {
                        NavigationRail(
                            selectedItem = selectedItem,
                            onNavigationItemClick = { index ->
                                selectedItem = index
                                navigationUIHandler.onNavigationItemClick(selectedItem)
                            }
                        )
                    }
                    WindowWidthSizeClass.Expanded -> {
                        NavigationDrawer(
                            selectedItem = selectedItem,
                            onNavigationItemClick = { index ->
                                selectedItem = index
                                navigationUIHandler.onNavigationItemClick(selectedItem)
                            }
                        )
                    }
                    else -> {}
                }
                ChildNavHost(
                    onSettingClicked = onSettingClicked,
                    navigationUIHandler = navigationUIHandler,
                    bottomSheetState = bottomSheetState,
                    snackbarHostState = snackbarHostState,
                )
            }
        },
    )
}


@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun DashboardScreenCompactPreview() {
    BucksTheme {
        DashboardScaffold(
            widthSize = WindowWidthSizeClass.Compact,
            onSettingClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, widthDp = 700)
@Composable
fun DashboardScreenMediumPreview() {
    BucksTheme {
        DashboardScaffold(
            widthSize = WindowWidthSizeClass.Medium,
            onSettingClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
fun DashboardScreenExpandedPreview() {
    BucksTheme {
        DashboardScaffold(
            widthSize = WindowWidthSizeClass.Expanded,
            onSettingClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        )
    }
}



