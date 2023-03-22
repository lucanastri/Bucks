package com.kizune.bucks.ui.navigation

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.kizune.bucks.model.Fund
import com.kizune.bucks.ui.DashboardDestination
import com.kizune.bucks.ui.checking.CheckingDestination
import com.kizune.bucks.ui.editfund.EditFundDestination
import com.kizune.bucks.ui.insertfund.InsertFundDestination
import com.kizune.bucks.ui.insertmovement.InsertMovementDestination


class NavigationUIHandler(
    val parentNavController: NavHostController,
    val childNavController: NavHostController,
    val moveTaskToBack: () -> Unit = {},
    val startDestination: String = DashboardDestination.route,
) : NavigationUIEvents {
    override fun onInsertMovementClicked(fund: Fund) {
        parentNavController.navigate("${InsertMovementDestination.route}/${fund.fundID}")
    }

    override fun onCardClicked(fund: Fund) {
        parentNavController.navigate("${CheckingDestination.route}/${fund.fundID}")
    }

    override fun onEditClicked(fund: Fund) {
        parentNavController.navigate("${EditFundDestination.route}/${fund.fundID}")
    }

    override fun onPlaceholderClicked() {
        parentNavController.navigate(InsertFundDestination.route)
    }

    override fun onNavigateUp() {
        parentNavController.navigateUp()
    }

    override fun onNavigationItemClick(index: Int) {
        childNavController.navigate(navigationItems[index].route) {
            popUpTo(childNavController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    override fun onMoveTaskToBack() {
        moveTaskToBack()
    }

}