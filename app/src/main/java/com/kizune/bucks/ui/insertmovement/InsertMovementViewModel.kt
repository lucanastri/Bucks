package com.kizune.bucks.ui.insertmovement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.Movement
import com.kizune.bucks.model.MovementType
import com.kizune.bucks.ui.checking.CheckingDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
@Singleton
class InsertMovementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val fundID: Long = checkNotNull(savedStateHandle[CheckingDestination.fundIDArg])

    val fundUiState: StateFlow<FundMovementUiState> =
        databaseRepository.getFundStream(fundID)
            .filterNotNull()
            .map { fund ->
                updateUiState(MovementDetails(sourceFund = fund))
                FundMovementUiState(fund)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FundMovementUiState()
            )

    val fundListUiState: StateFlow<FundListUiState> =
        databaseRepository.getFundsStream()
            .filterNotNull()
            .map { list ->
                FundListUiState(list)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = FundListUiState()
            )

    var movementUiState by mutableStateOf(InsertMovementUiState())
        private set

    fun updateUiState(movementDetails: MovementDetails) {
        movementUiState =
            InsertMovementUiState(
                movementDetails = movementDetails,
                isMovementValid = validateInput(movementDetails)
            )
    }

    private fun validateInput(uiState: MovementDetails = movementUiState.movementDetails): Boolean {
        return with(uiState) {
            if (type == MovementType.In && receiverFund != null) {
                receiverFundTitle.isNotEmpty()
                        && title.isNotEmpty()
                        && description.isNotEmpty()
                        && amountValue <= receiverFund.cash
                        && amountValue > 0.0
            } else if (type == MovementType.Out) {
                receiverFundTitle.isNotEmpty()
                        && title.isNotEmpty()
                        && description.isNotEmpty()
                        && amountValue <= sourceFund.cash
                        && amountValue > 0.0
            } else {
                receiverFundTitle.isNotEmpty()
                        && title.isNotEmpty()
                        && description.isNotEmpty()
                        && amountValue > 0.0
            }
        }
    }

    suspend fun insertMovement() {
        val movementDetails = movementUiState.movementDetails
        if (movementDetails.type == MovementType.In) {
            movementDetails.sourceFund.cash =
                movementDetails.sourceFund.cash.plus(movementDetails.amountValue)
            movementDetails.receiverFund?.cash =
                movementDetails.receiverFund?.cash?.minus(movementDetails.amountValue) ?: 0.0
        } else {
            movementDetails.sourceFund.cash =
                movementDetails.sourceFund.cash.minus(movementDetails.amountValue)
            movementDetails.receiverFund?.cash =
                movementDetails.receiverFund?.cash?.plus(movementDetails.amountValue) ?: 0.0
        }
        databaseRepository.insertMovement(movementDetails.toMovement())
        databaseRepository.updateFund(movementDetails.sourceFund)
        databaseRepository.updateFund(movementDetails.receiverFund ?: Fund())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class InsertMovementUiState(
    val movementDetails: MovementDetails = MovementDetails(),
    val isMovementValid: Boolean = false,
)

data class MovementDetails(
    val sourceFund: Fund = Fund(),
    val receiverFund: Fund? = null,
    val receiverFundTitle: String = "",
    val title: String = "",
    val description: String = "",
    val type: MovementType = MovementType.In,
    val amount: String = "",
    val amountValue: Double = 0.0,
    val date: Long = 0
)

fun MovementDetails.toMovement() = Movement(
    movementID = UUID.randomUUID().leastSignificantBits,
    fundInID = if (type == MovementType.In) sourceFund.fundID else receiverFund?.fundID,
    fundOutID = if (type == MovementType.Out) sourceFund.fundID else receiverFund?.fundID,
    title = title,
    description = description,
    amount = amountValue,
    date = System.currentTimeMillis(),
)

data class FundMovementUiState(
    val sourceFund: Fund = Fund(),
)

data class FundListUiState(
    val funds: List<Fund> = listOf()
)


