package com.kizune.bucks.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kizune.bucks.R
import com.kizune.bucks.ui.bottomsheet.BottomSheetType
import com.kizune.bucks.ui.cards.CardsScreen
import com.kizune.bucks.ui.dialog.DialogsViewModel
import com.kizune.bucks.ui.dialog.FundChooserDialog
import com.kizune.bucks.ui.dialog.OperationDialog
import com.kizune.bucks.ui.report.ReportScreen
import com.kizune.bucks.ui.settings.SettingsScreen
import kotlinx.coroutines.launch

sealed class DashboardScreens(
    val route: String,
    @StringRes val resourceId: Int,
    val imageVector: ImageVector
) {
    object Cards : DashboardScreens("Cards", R.string.nav_cards, Icons.Rounded.CreditCard)
    object Report : DashboardScreens("Report", R.string.nav_report, Icons.Rounded.Assignment)
    object Settings : DashboardScreens("Setting", R.string.nav_settings, Icons.Rounded.Settings)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChildNavHost(
    onSettingClicked: (BottomSheetType) -> Unit,
    navigationUIHandler: NavigationUIHandler,
    bottomSheetState: ModalBottomSheetState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    dialogsViewModel: DialogsViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler {
        if (bottomSheetState.isVisible) {
            coroutineScope.launch {
                bottomSheetState.hide()
            }
        } else {
            navigationUIHandler.onMoveTaskToBack()
        }
    }

    if (dialogsViewModel.showOperationDialog) {
        OperationDialog(navigationUIHandler = navigationUIHandler)
    }

    if (dialogsViewModel.showFundsDialog) {
        FundChooserDialog(navigationUIHandler = navigationUIHandler)
    }

    val onFabClicked: () -> Unit = {
        dialogsViewModel.showOperationDialog = true
    }

    NavHost(
        navController = navigationUIHandler.childNavController,
        startDestination = DashboardScreens.Cards.route,
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        composable(route = DashboardScreens.Cards.route) {
            CardsScreen(
                onFabClicked = onFabClicked,
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(route = DashboardScreens.Report.route) {
            ReportScreen(
                navigationUIHandler = navigationUIHandler,
                modifier = modifier
            )
        }
        composable(route = DashboardScreens.Settings.route) {
            SettingsScreen(
                onSettingClicked = onSettingClicked,
                snackbarHostState = snackbarHostState,
                modifier = modifier
            )
        }
    }
}