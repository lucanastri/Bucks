package com.kizune.bucks.ui.insertfund

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.flowlayout.FlowRow
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.BucksCard
import com.kizune.bucks.ui.BucksChip
import com.kizune.bucks.ui.BucksFilledButton
import com.kizune.bucks.ui.BucksTopBar
import com.kizune.bucks.ui.navigation.Destination
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.utils.serialNumberFormatter
import kotlinx.coroutines.launch
import java.util.*

object InsertFundDestination : Destination {
    override val route = "Insertfund"
}

@Composable
fun InsertFundScreen(
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    viewModel: InsertFundViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val insertUIState = viewModel.insertUiState
    val fund = insertUIState.fund
    val isFundValid = insertUIState.isFundValid

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
            viewModel.insertFund()
            navigationUIHandler.onNavigateUp()
        }
    }


    InsertFundScreenContent(
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
fun InsertFundScreenContent(
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
    modifier: Modifier = Modifier
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
                title = stringResource(R.string.add_card),
                imageVector = Icons.Rounded.ArrowBack,
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

@Composable
fun Section(
    header: @Composable () -> Unit,
    field: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Spacer(Modifier.height(24.dp))
        header()
        Spacer(Modifier.height(16.dp))
        field()
    }
}

@Composable
fun SectionItemText(
    @StringRes title: Int,
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    Section(
        header = {
            HeaderText(text = title)
        },
        field = {
            FieldInputText(
                placeholder = placeholder,
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
            )
        },
        modifier = modifier
    )
}

@Composable
fun SectionItemText(
    @StringRes title: Int,
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
    modifier: Modifier = Modifier
) {
    Section(
        header = {
            HeaderText(text = title)
        },
        field = {
            FieldInputText(
                placeholder = placeholder,
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                visualTransformation = visualTransformation
            )
        },
        modifier = modifier
    )
}

@Composable
fun SectionItemChip(
    @StringRes title: Int,
    value: Int,
    items: List<Int>,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Section(
        header = {
            HeaderText(text = title)
        },
        field = {
            ChipGroup(
                items = items,
                value = value,
                onChipSelected = onChipSelected
            )
        },
        modifier = modifier
    )
}

@Composable
fun HeaderText(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldInputText(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { FieldInputPlaceholder(text = placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldInputText(
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    visualTransformation: VisualTransformation,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { FieldInputPlaceholder(text = placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        modifier = modifier
            .fillMaxWidth()
    )
}

@Composable
fun FieldInputPlaceholder(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipGroup(
    items: List<Int>,
    value: Int,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        mainAxisSpacing = 8.dp,
        crossAxisSpacing = 0.dp,
        modifier = modifier
            .fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            BucksChip(
                item = item,
                index = index,
                selected = value == index,
                onChipSelected = { onChipSelected(index) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun InsertFundScreenContentCompactPreview() {
    BucksTheme {
        InsertFundScreenContent(
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
fun InsertFundScreenContentMediumPreview() {
    BucksTheme {
        InsertFundScreenContent(
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
fun InsertFundScreenContentExpandedPreview() {
    BucksTheme {
        InsertFundScreenContent(
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