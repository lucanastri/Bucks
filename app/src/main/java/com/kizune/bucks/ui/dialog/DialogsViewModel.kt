package com.kizune.bucks.ui.dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kizune.bucks.model.Fund
import com.kizune.bucks.ui.checking.CheckingViewModel
import com.kizune.bucks.ui.insertfund.InsertFundDestination
import com.kizune.bucks.ui.navigation.NavigationUIEvents
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
@Singleton
class DialogsViewModel @Inject constructor(

) : ViewModel() {
    var showOperationDialog by mutableStateOf(false)
    var showFundsDialog by mutableStateOf(false)
    var showDeleteFundDialog by mutableStateOf(false)

    fun operationDismiss() {
        showOperationDialog = false
    }

    fun operationConfirm(
        navigationUIHandler: NavigationUIHandler,
        operation: Operation,
    ) {
        showOperationDialog = false
        when (operation) {
            Operation.INSERT_FUND -> {
                navigationUIHandler.parentNavController.navigate(InsertFundDestination.route)
            }
            Operation.INSERT_MOVEMENT -> {
                showFundsDialog = true
            }
        }
    }

    fun fundsDismiss() {
        showFundsDialog = false
    }

    fun fundsConfirm(
        navigationUIHandler: NavigationUIEvents,
        fund: Fund
    ) {
        showFundsDialog = false
        navigationUIHandler.onInsertMovementClicked(fund)
    }

    fun deleteFundDismiss() {
        showDeleteFundDialog = false
    }

    fun deleteFundConfirm(
        coroutineScope: CoroutineScope,
        navigationUIHandler: NavigationUIHandler,
        viewModel: CheckingViewModel,
    ) {
        showDeleteFundDialog = false
        coroutineScope.launch {
            viewModel.deleteFund()
            navigationUIHandler.onNavigateUp()
        }
    }
}

