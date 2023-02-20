package com.kizune.bucks.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.MovementType
import com.kizune.bucks.ui.settings.SettingsViewModel
import com.kizune.bucks.utils.convertToFormattedDate
import com.kizune.bucks.utils.getFormattedCash

@Composable
fun CashText(
    value: Double,
    style: TextStyle,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    Text(
        text = value.getFormattedCash(getCurrency()),
        style = style,
        maxLines = maxLines,
        overflow = overflow,
        color = color,
        modifier = modifier,
    )
}

@Composable
fun CashSupportingText(
    fund: Fund,
    selectedFund: Fund?,
    movementType: MovementType,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val supportingText = when (movementType) {
        MovementType.Out -> stringResource(id = R.string.add_movement_amount_supporting) +
                " ${fund.cash.getFormattedCash(getCurrency())}"
        MovementType.In -> {
            if (selectedFund != null)
                stringResource(id = R.string.add_movement_amount_supporting) +
                        " ${selectedFund.cash.getFormattedCash(getCurrency())}" else ""
        }
    }
    Text(
        text = supportingText,
        style = style,
        modifier = modifier
    )
}

@Composable
fun DateText(
    value: Long,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = value.convertToFormattedDate(getDateFormat()),
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
private fun getCurrency(): Int {
    return if (LocalInspectionMode.current) {
        0
    } else {
        val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
        viewModel.preferenceUiState.collectAsState().value.currencyPreference
    }
}

@Composable
private fun getDateFormat(): Int {
    return if (LocalInspectionMode.current) {
        0
    } else {
        val viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
        viewModel.preferenceUiState.collectAsState().value.dateFormatPreference
    }
}