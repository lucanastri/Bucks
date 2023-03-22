package com.kizune.bucks.ui.editfund

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.BucksCard
import com.kizune.bucks.ui.BucksFilledButton
import com.kizune.bucks.ui.BucksTopBar
import com.kizune.bucks.ui.insertfund.SectionItemChip
import com.kizune.bucks.ui.insertfund.SectionItemText
import com.kizune.bucks.ui.navigation.Destination
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.utils.serialNumberFormatter
import kotlinx.coroutines.launch
import java.util.*

object EditFundDestination : Destination {
    override val route: String = "EditFund"
    const val fundIDArg = "fundID"
    val routeWithArgs = "${route}/{$fundIDArg}"
}

@Composable
fun EditFundScreen(
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    viewModel: EditFundViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val editUiState = viewModel.editUiState
    val isFundValid = editUiState.isFundValid
    val fund = editUiState.fund

    val coroutineScope = rememberCoroutineScope()

    val onTitleChanged: (String) -> Unit = {
        viewModel.updateUiState(fund.copy(title = it))
    }

    val onTypeSelected: (Int) -> Unit = {
        viewModel.updateUiState(fund.copy(type = enumValues<FundType>()[it]))
    }

    val onCategorySelected: (Int) -> Unit = {
        viewModel.updateUiState(fund.copy(category = enumValues<FundCategory>()[it]))
    }

    val onBrandChanged: (String) -> Unit = {
        viewModel.updateUiState(fund.copy(brand = it))
    }

    val onBankSelected: (Int) -> Unit = {
        viewModel.updateUiState(fund.copy(bank = enumValues<FundBank>()[it]))
    }

    val onIBANChanged: (String) -> Unit = {
        viewModel.updateUiState(fund.copy(iban = it))
    }

    val onSerialChanged: (String) -> Unit = {
        viewModel.updateUiState(fund.copy(serialNumber = it.take(16)))
    }

    val onNetworkSelected: (Int) -> Unit = {
        viewModel.updateUiState(fund.copy(network = enumValues<FundNetwork>()[it]))
    }

    val onConfirmButtonClicked: () -> Unit = {
        coroutineScope.launch {
            viewModel.updateFund()
            navigationUIHandler.onNavigateUp()
        }
    }

    EditFundScreenContent(
        navigationUIHandler = navigationUIHandler,
        fund = fund,
        isFundValid = isFundValid,
        onTitleChanged = onTitleChanged,
        onTypeSelected = onTypeSelected,
        onCategorySelected = onCategorySelected,
        onBrandChanged = onBrandChanged,
        onBankSelected = onBankSelected,
        onIBANChanged = onIBANChanged,
        onSerialChanged = onSerialChanged,
        onNetworkSelected = onNetworkSelected,
        onConfirmButtonClicked = onConfirmButtonClicked,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFundScreenContent(
    navigationUIHandler: NavigationUIHandler,
    fund: Fund,
    isFundValid: Boolean,
    onTitleChanged: (String) -> Unit,
    onTypeSelected: (Int) -> Unit,
    onCategorySelected: (Int) -> Unit,
    onBrandChanged: (String) -> Unit,
    onBankSelected: (Int) -> Unit,
    onIBANChanged: (String) -> Unit,
    onSerialChanged: (String) -> Unit,
    onNetworkSelected: (Int) -> Unit,
    onConfirmButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberScrollState()

    val keyboardOptionsSentences = KeyboardOptions.Default.copy(
        capitalization = KeyboardCapitalization.Sentences,
        autoCorrect = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Done
    )

    val keyboardOptionsWords = keyboardOptionsSentences.copy(
        capitalization = KeyboardCapitalization.Words,
    )

    val keyboardOptionsCharacters = keyboardOptionsSentences.copy(
        capitalization = KeyboardCapitalization.Characters,
    )

    val keyboardOptionsNumbers = KeyboardOptions(
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done
    )

    val keyboardActions = KeyboardActions(
        onDone = {
            focusManager.clearFocus()
        },
        onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    Scaffold(
        topBar = {
            BucksTopBar(
                title = stringResource(R.string.edit_card),
                imageVector = Icons.Rounded.Close,
                onBackClicked = { navigationUIHandler.onNavigateUp() }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(state = listState)
                .padding(padding)
                .padding(16.dp)
        ) {
            BucksCard(fund = fund)
            SectionItemText(
                title = R.string.title,
                placeholder = R.string.title_placeholder,
                value = fund.title,
                onValueChange = { onTitleChanged(it) },
                keyboardOptions = keyboardOptionsSentences,
                keyboardActions = keyboardActions,
            )
            SectionItemChip(
                title = R.string.type,
                value = fund.type.ordinal,
                items = enumValues<FundType>().map { it.id },
                onChipSelected = { onTypeSelected(it) },
            )
            SectionItemChip(
                title = R.string.category,
                value = fund.category.ordinal,
                items = enumValues<FundCategory>().map { it.id },
                onChipSelected = { onCategorySelected(it) },
            )

            when (fund.type) {
                FundType.Wallet -> {
                    SectionItemText(
                        title = R.string.brand,
                        placeholder = R.string.brand_placeholder,
                        value = fund.brand,
                        onValueChange = { onBrandChanged(it) },
                        keyboardOptions = keyboardOptionsWords,
                        keyboardActions = keyboardActions
                    )
                }
                FundType.BankAccount -> {
                    SectionItemChip(
                        title = R.string.bank,
                        value = fund.bank.ordinal,
                        items = enumValues<FundBank>().map { it.id },
                        onChipSelected = { onBankSelected(it) },
                    )
                    SectionItemText(
                        title = R.string.iban,
                        placeholder = R.string.iban_placeholder,
                        value = fund.iban,
                        onValueChange = { onIBANChanged(it) },
                        keyboardOptions = keyboardOptionsCharacters,
                        keyboardActions = keyboardActions,
                    )
                }
                FundType.CreditCard,
                FundType.DebitCard,
                FundType.PrepaidCard -> {
                    SectionItemText(
                        title = R.string.serial,
                        placeholder = R.string.serial_placeholder,
                        value = fund.serialNumber,
                        onValueChange = { onSerialChanged(it) },
                        keyboardOptions = keyboardOptionsNumbers,
                        keyboardActions = keyboardActions,
                        visualTransformation = { serialNumberFormatter(it) }
                    )
                    SectionItemChip(
                        title = R.string.bank,
                        value = fund.bank.ordinal,
                        items = enumValues<FundBank>().map { it.id },
                        onChipSelected = { onBankSelected(it) },
                    )
                    SectionItemChip(
                        title = R.string.network,
                        value = fund.network.ordinal,
                        items = enumValues<FundNetwork>().map { it.id },
                        onChipSelected = { onNetworkSelected(it) },
                    )
                }
                else -> {}
            }
            Spacer(Modifier.height(24.dp))
            BucksFilledButton(
                text = R.string.save_card,
                enabled = isFundValid,
                onClick = onConfirmButtonClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditFundScreenContentCompactPreview() {
    BucksTheme {
        EditFundScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            fund = mockPrepaid,
            isFundValid = true,
            onTitleChanged = {},
            onTypeSelected = {},
            onCategorySelected = {},
            onBrandChanged = {},
            onBankSelected = {},
            onIBANChanged = {},
            onSerialChanged = {},
            onNetworkSelected = {},
            onConfirmButtonClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun EditFundScreenContentMediumPreview() {
    BucksTheme {
        EditFundScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            fund = mockPrepaid,
            isFundValid = true,
            onTitleChanged = {},
            onTypeSelected = {},
            onCategorySelected = {},
            onBrandChanged = {},
            onBankSelected = {},
            onIBANChanged = {},
            onSerialChanged = {},
            onNetworkSelected = {},
            onConfirmButtonClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
fun EditFundScreenContentExpandedPreview() {
    BucksTheme {
        EditFundScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            fund = mockPrepaid,
            isFundValid = true,
            onTitleChanged = {},
            onTypeSelected = {},
            onCategorySelected = {},
            onBrandChanged = {},
            onBankSelected = {},
            onIBANChanged = {},
            onSerialChanged = {},
            onNetworkSelected = {},
            onConfirmButtonClicked = {}
        )
    }
}