package com.kizune.bucks.ui.checking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundWithMovements
import com.kizune.bucks.model.Movement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
@Singleton
class CheckingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private var fundID: Long = checkNotNull(savedStateHandle[CheckingDestination.fundIDArg])

    val checkingUiState: StateFlow<CheckingUiState> =
        databaseRepository.getCompleteFundStream(fundID)
            .map { fund ->
                //Utile per evitare una NullPointerException
                if (fund != null) {
                    CheckingUiState(fund, CheckingUiStateResult.Success)
                } else {
                    CheckingUiState()
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CheckingUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deleteFund() {
        databaseRepository.deleteFund(checkingUiState.value.fund.fund)
    }

    suspend fun deleteMovement(movement: Movement) {
        if (movement.fundInID != null) {
            val fund = databaseRepository.getFund(movement.fundInID)
            fund.cash -= movement.amount
            databaseRepository.updateFund(fund)
        }
        if (movement.fundOutID != null) {
            val fund = databaseRepository.getFund(movement.fundOutID)
            fund.cash += movement.amount
            databaseRepository.updateFund(fund)
        }
        databaseRepository.deleteMovement(movement)
    }
}

data class CheckingUiState(
    val fund: FundWithMovements =
        FundWithMovements(
            fund = Fund(),
            movementsIn = listOf(),
            movementsOut = listOf()
        ),
    val result: CheckingUiStateResult = CheckingUiStateResult.Loading
)

interface CheckingUiStateResult {
    object Success : CheckingUiStateResult
    object Loading : CheckingUiStateResult
}