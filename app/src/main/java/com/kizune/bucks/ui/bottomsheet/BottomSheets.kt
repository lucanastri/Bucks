package com.kizune.bucks.ui.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.util.*

enum class BottomSheetType {
    CURRENCY,
    DATEFORMAT,
    REPORTFILTER,
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheet(
    bottomSheetState: ModalBottomSheetState,
    bottomSheetType: BottomSheetType,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.preferenceUiState.collectAsState().value
    val currencyPreference = uiState.currencyPreference
    val dateFormatPreference = uiState.dateFormatPreference
    val reportFilterPreference = uiState.reportFilterPreference
    val coroutineScope = rememberCoroutineScope()

    when (bottomSheetType) {
        BottomSheetType.CURRENCY -> {
            BottomSheetCurrency(
                selected = currencyPreference,
                onItemClick = { currency ->
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        viewModel.setCurrency(currency.ordinal)
                    }
                }
            )
        }
        BottomSheetType.DATEFORMAT -> {
            BottomSheetDateFormat(
                selected = dateFormatPreference,
                onItemClick = { dateFormat ->
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        viewModel.setDateFormat(dateFormat.ordinal)
                    }
                }
            )
        }
        BottomSheetType.REPORTFILTER -> {
            BottomSheetReportFilter(selected = reportFilterPreference,
                onItemClick = { reportFilter ->
                    coroutineScope.launch {
                        bottomSheetState.hide()
                        viewModel.setReportFilter(reportFilter.ordinal)
                    }
                }
            )
        }
    }
}

@Composable
fun BottomSheetIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier.size(18.dp)
    )
}