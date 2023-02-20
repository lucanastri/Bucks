package com.kizune.bucks.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.FundWithMovements
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
@Singleton
class ReportViewModel @Inject constructor(
    databaseRepository: DatabaseRepository
) : ViewModel() {

    val reportUiState: StateFlow<ReportUiState> =
        databaseRepository.getCompleteFundsStream()
            .map { list ->
                val result =
                    if (list.isEmpty()) ReportUiStateResult.Empty
                    else ReportUiStateResult.Success
                ReportUiState(list, result)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ReportUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ReportUiState(
    val list: List<FundWithMovements> = listOf(),
    val result: ReportUiStateResult = ReportUiStateResult.Loading
)

interface ReportUiStateResult {
    object Success : ReportUiStateResult
    object Loading : ReportUiStateResult
    object Empty : ReportUiStateResult
}