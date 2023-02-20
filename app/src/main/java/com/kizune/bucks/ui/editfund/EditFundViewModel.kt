package com.kizune.bucks.ui.editfund

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundType
import com.kizune.bucks.ui.checking.CheckingDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@HiltViewModel
@Singleton
class EditFundViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val fundID: Long = checkNotNull(savedStateHandle[CheckingDestination.fundIDArg])

    internal val uiState: StateFlow<EditUiState> =
        databaseRepository.getFundStream(fundID)
            .filterNotNull()
            .map { fund ->
                updateUiState(fund)
                EditUiState(fund, true)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = EditUiState()
            )


    var editUiState by mutableStateOf(EditUiState())
        private set

    fun updateUiState(fund: Fund) {
        editUiState = EditUiState(fund = fund, isFundValid = validateInput(fund))
    }

    private fun validateInput(uiState: Fund = editUiState.fund): Boolean {
        return with(uiState) {
            when (type) {
                FundType.BankAccount -> title.isNotBlank() && iban.isNotBlank()
                FundType.CreditCard,
                FundType.DebitCard,
                FundType.PrepaidCard -> title.isNotBlank() && serialNumber.isNotBlank()
                else -> title.isNotBlank()
            }
        }
    }

    suspend fun updateFund() {
        databaseRepository.updateFund(editUiState.fund)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class EditUiState(
    val fund: Fund = Fund(fundID = abs(UUID.randomUUID().leastSignificantBits)),
    val isFundValid: Boolean = false,
)
