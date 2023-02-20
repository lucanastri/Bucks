package com.kizune.bucks.ui.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.Fund
import com.kizune.bucks.ui.BucksFilledButton
import com.kizune.bucks.ui.BucksFundIcon
import com.kizune.bucks.ui.BucksTextButton
import com.kizune.bucks.ui.cards.CardsUiStateResult
import com.kizune.bucks.ui.cards.CardsViewModel
import com.kizune.bucks.ui.navigation.NavigationUIHandler

@Composable
fun AlertDialog(
    @StringRes title: Int,
    @StringRes subtitle: Int
) {
    Dialog(
        onDismissRequest = { },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

enum class Operation(
    val icon: ImageVector,
    @StringRes val title: Int,
    @StringRes val subtitle: Int
) {
    INSERT_FUND(
        Icons.Rounded.CreditCard,
        R.string.operation_insertfund_title,
        R.string.operation_insertfund_subtitle
    ),
    INSERT_MOVEMENT(
        Icons.Rounded.SwapHoriz,
        R.string.operation_insertmovement_title,
        R.string.operation_insertmovement_subtitle
    )
}

@Composable
fun OperationDialog(
    navigationUIHandler: NavigationUIHandler,
    viewModel: DialogsViewModel = viewModel()
) {
    Dialog(
        onDismissRequest = { viewModel.operationDismiss() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.operation_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
                OperationItem(
                    operation = Operation.INSERT_FUND,
                    modifier = Modifier
                        .clickable {
                            viewModel.operationConfirm(
                                navigationUIHandler = navigationUIHandler,
                                operation = Operation.INSERT_FUND
                            )
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
                OperationItem(
                    operation = Operation.INSERT_MOVEMENT,
                    modifier = Modifier
                        .clickable {
                            viewModel.operationConfirm(
                                navigationUIHandler = navigationUIHandler,
                                operation = Operation.INSERT_MOVEMENT
                            )
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    )
}

@Composable
fun OperationItem(
    operation: Operation,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Icon(
            imageVector = operation.icon,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null
        )
        Spacer(Modifier.width(16.dp))
        OperationText(
            operation = operation,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null
        )
    }
}

@Composable
fun OperationText(
    operation: Operation,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = operation.title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(id = operation.subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun FundChooserDialog(
    navigationUIHandler: NavigationUIHandler,
    cardsViewModel: CardsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dialogsViewModel: DialogsViewModel = viewModel()
) {
    val cardsUiState by cardsViewModel.cardsUiState.collectAsState()
    val result = cardsUiState.result
    val list = cardsUiState.fundList
    when (result) {
        CardsUiStateResult.Success -> {
            Dialog(
                onDismissRequest = { dialogsViewModel.fundsDismiss() },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(id = R.string.fund_chooser_dialog_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Divider(color = MaterialTheme.colorScheme.outline)
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(0.dp),
                            contentPadding = PaddingValues(vertical = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(list) { fund ->
                                DialogFundItem(
                                    fund = fund,
                                    modifier = Modifier
                                        .clickable {
                                            dialogsViewModel.fundsConfirm(
                                                navigationUIHandler,
                                                fund
                                            )
                                        }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
        CardsUiStateResult.Empty -> {
            dialogsViewModel.fundsDismiss()
        }
        CardsUiStateResult.Loading -> {}
    }
}

@Composable
fun DialogFundItem(
    fund: Fund,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        BucksFundIcon(fund = fund)
        Spacer(Modifier.width(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = fund.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(id = fund.type.id),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun DeleteFundDialog(
    onDeleteConfirmClicked: () -> Unit,
    dialogsViewModel: DialogsViewModel = viewModel()
) {
    Dialog(
        onDismissRequest = { dialogsViewModel.deleteFundDismiss() },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.delete_fund_title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.delete_fund_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                BucksFilledButton(
                    text = R.string.delete_fund_confirm,
                    onClick = onDeleteConfirmClicked,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                BucksTextButton(
                    text = R.string.cancel,
                    onClick = { dialogsViewModel.deleteFundDismiss() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}