package com.kizune.bucks

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kizune.bucks.ui.cards.CardsViewModel
import com.kizune.bucks.ui.checking.CheckingViewModel
import com.kizune.bucks.ui.editfund.EditFundViewModel
import com.kizune.bucks.ui.insertfund.InsertFundViewModel
import com.kizune.bucks.ui.insertmovement.InsertMovementViewModel
import com.kizune.bucks.ui.report.ReportViewModel
import com.kizune.bucks.ui.settings.BackupViewModel
import com.kizune.bucks.ui.settings.SettingsViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            CardsViewModel(bucksApplication().container.databaseRepository)
        }
        initializer {
            InsertFundViewModel(bucksApplication().container.databaseRepository)
        }
        initializer {
            ReportViewModel(bucksApplication().container.databaseRepository)
        }
        initializer {
            BackupViewModel(bucksApplication().container.databaseRepository)
        }
        initializer {
            SettingsViewModel(bucksApplication().sharedUserPreferenceRepository)
        }
        initializer {
            CheckingViewModel(
                this.createSavedStateHandle(),
                bucksApplication().container.databaseRepository
            )
        }
        initializer {
            EditFundViewModel(
                this.createSavedStateHandle(),
                bucksApplication().container.databaseRepository
            )
        }
        initializer {
            InsertMovementViewModel(
                this.createSavedStateHandle(),
                bucksApplication().container.databaseRepository
            )
        }
    }
}

fun CreationExtras.bucksApplication(): BucksApplicationClass =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BucksApplicationClass)