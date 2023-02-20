package com.kizune.bucks.ui.insertfund

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundType
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@HiltViewModel
@Singleton
class InsertFundViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    var insertUiState by mutableStateOf(InsertUIState())
        private set

    fun updateUiState(fund: Fund) {
        insertUiState = InsertUIState(fund = fund, isFundValid = validateInput(fund))
    }

    private fun validateInput(uiState: Fund = insertUiState.fund): Boolean {
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

    suspend fun insertFund() {
        databaseRepository.insertFund(insertUiState.fund)
    }
}

data class InsertUIState(
    val fund: Fund = Fund(fundID = abs(UUID.randomUUID().leastSignificantBits)),
    val isFundValid: Boolean = false,
)

fun Fund.toInsertUiState(isFundValid: Boolean = false): InsertUIState =
    InsertUIState(this.copy(fundID = UUID.randomUUID().leastSignificantBits))
