package com.kizune.bucks.ui.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CardsViewModel(
    databaseRepository: DatabaseRepository
) : ViewModel() {
    val cardsUiState: StateFlow<CardsUiState> =
        databaseRepository.getFundsStream()
            .map { list ->
                val result =
                    if (list.isEmpty()) CardsUiStateResult.Empty
                    else CardsUiStateResult.Success
                CardsUiState(list, result)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = CardsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class CardsUiState(
    val fundList: List<Fund> = listOf(),
    val result: CardsUiStateResult = CardsUiStateResult.Loading
)

interface CardsUiStateResult {
    object Success : CardsUiStateResult
    object Loading : CardsUiStateResult
    object Empty : CardsUiStateResult
}