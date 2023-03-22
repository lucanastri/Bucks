package com.kizune.bucks.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.data.SharedUserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
@Singleton
class SettingsViewModel @Inject constructor(
    private val sharedUserPreferenceRepository: SharedUserPreferenceRepository
) : ViewModel() {
    val preferenceUiState: StateFlow<SettingsUiState> =
        sharedUserPreferenceRepository.preference.map { preferences ->
            SettingsUiState(
                onBoardingPreference = preferences.onBoarding,
                currencyPreference = preferences.currency,
                dateFormatPreference = preferences.dateFormat,
                reportFilterPreference = preferences.reportfilter,
                backupCreationPreference = preferences.backupCreation,
                backupRecoverPreference = preferences.backupRecover,
                result = SettingsUiStateResult.Success
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = SettingsUiState()
        )

    fun setOnBoarding(flag: Boolean) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setOnBoarding(flag)
        }
    }

    fun setCurrency(flag: Int) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setCurrency(flag)
        }
    }

    fun setDateFormat(flag: Int) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setDateFormat(flag)
        }
    }

    fun setReportFilter(flag: Int) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setReportFilter(flag)
        }
    }

    fun setBackupCreation(flag: Long) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setBackupCreation(flag)
        }
    }

    fun setBackupRecover(flag: Long) {
        viewModelScope.launch {
            sharedUserPreferenceRepository.setBackupRecover(flag)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

interface SettingsUiStateResult {
    object Success : SettingsUiStateResult
    object Loading : SettingsUiStateResult
}

data class SettingsUiState(
    val onBoardingPreference: Boolean = false,
    val currencyPreference: Int = 0,
    val dateFormatPreference: Int = 0,
    val reportFilterPreference: Int = 0,
    val backupCreationPreference: Long = 0,
    val backupRecoverPreference: Long = 0,
    val result: SettingsUiStateResult = SettingsUiStateResult.Loading
)
