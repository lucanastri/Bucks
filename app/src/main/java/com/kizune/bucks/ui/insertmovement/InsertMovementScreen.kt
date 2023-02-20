package com.kizune.bucks.ui.insertmovement

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EuroSymbol
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.MovementType
import com.kizune.bucks.model.mockFunds
import com.kizune.bucks.model.mockPrepaid
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.BucksFilledButton
import com.kizune.bucks.ui.BucksFundIcon
import com.kizune.bucks.ui.BucksTopBar
import com.kizune.bucks.ui.CashSupportingText
import com.kizune.bucks.ui.insertfund.ChipGroup
import com.kizune.bucks.ui.insertfund.FieldInputPlaceholder
import com.kizune.bucks.ui.insertfund.HeaderText
import com.kizune.bucks.ui.navigation.Destination
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import kotlinx.coroutines.launch

object InsertMovementDestination : Destination {
    override val route = "InsertMovement"
    const val fundIDArg = "fundID"
    val routeWithArgs = "${route}/{$fundIDArg}"
}

@Composable
fun InsertMovementScreen(
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    viewModel: InsertMovementViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    val insertMovementUiState = viewModel.movementUiState
    val fundUiState by viewModel.fundUiState.collectAsState()
    val senderFund = fundUiState.sourceFund
    val fundListUiState by viewModel.fundListUiState.collectAsState()
    val funds = fundListUiState.funds.minus(senderFund)
    val movementDetails = insertMovementUiState.movementDetails
    val isMovementValid = insertMovementUiState.isMovementValid

    val onMovementTypeSelected: (Int) -> Unit = {
        viewModel.updateUiState(movementDetails.copy(type = enumValues<MovementType>()[it]))
    }

    val onReceiverFundChanged: (String) -> Unit = {
        viewModel.updateUiState(
            movementDetails.copy(
                receiverFund = null,
                receiverFundTitle = it
            )
        )
    }

    val onReceiverFundSelected: (Fund) -> Unit = {
        viewModel.updateUiState(
            movementDetails.copy(
                receiverFund = it,
                receiverFundTitle = it.title
            )
        )
    }

    val onTitleChanged: (String) -> Unit = {
        viewModel.updateUiState(movementDetails.copy(title = it))
    }

    val onDescriptionChanged: (String) -> Unit = {
        viewModel.updateUiState(movementDetails.copy(description = it))
    }

    val onAmountChanged: (String) -> Unit = {
        val value = it.replace("-", "")
        val amount = value.replace(",", ".")
        viewModel.updateUiState(
            movementDetails.copy(
                amount = amount,
                amountValue = amount.toDoubleOrNull() ?: 0.0
            )
        )
    }

    val onConfirmButtonClicked: () -> Unit = {
        coroutineScope.launch {
            viewModel.insertMovement()
            navigationUIHandler.onNavigateUp()
        }
    }

    InsertMovementScreenContent(
        navigationUIHandler = navigationUIHandler,
        senderFund = senderFund,
        funds = funds,
        isMovementValid = isMovementValid,
        movementDetails = movementDetails,
        onMovementTypeSelected = onMovementTypeSelected,
        onReceiverFundChanged = onReceiverFundChanged,
        onReceiverFundSelected = onReceiverFundSelected,
        onTitleChanged = onTitleChanged,
        onDescriptionChanged = onDescriptionChanged,
        onAmountChanged = onAmountChanged,
        onConfirmButtonClicked = onConfirmButtonClicked,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertMovementScreenContent(
    navigationUIHandler: NavigationUIHandler,
    senderFund: Fund,
    funds: List<Fund>,
    isMovementValid: Boolean,
    movementDetails: MovementDetails,
    onMovementTypeSelected: (Int) -> Unit,
    onReceiverFundChanged: (String) -> Unit,
    onReceiverFundSelected: (Fund) -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onConfirmButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val listState = rememberScrollState()

    Scaffold(
        topBar = {
            BucksTopBar(
                title = stringResource(id = R.string.add_movement),
                imageVector = Icons.Rounded.Close,
                onBackClicked = { navigationUIHandler.onNavigateUp() }
            )
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = listState)
                .padding(padding)
                .padding(16.dp)
        ) {
            Reference(fund = senderFund)
            Spacer(Modifier.height(16.dp))
            SectionMovement(
                header = { HeaderText(text = R.string.add_movement_type_label) },
                field = {
                    ChipGroup(
                        items = enumValues<MovementType>().map { it.id }.toList(),
                        value = movementDetails.type.ordinal,
                        onChipSelected = { onMovementTypeSelected(it) }
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
            SecondaryReference(
                amountType = movementDetails.type,
                allFunds = funds,
                value = movementDetails.receiverFundTitle,
                onValueChange = { onReceiverFundChanged(it) },
                onItemClick = { onReceiverFundSelected(it) },
                receiverFund = movementDetails.receiverFund,
            )
            Spacer(Modifier.height(16.dp))
            GenericField(
                label = R.string.add_movement_title_label,
                placeholder = R.string.add_movement_title_placeholder,
                value = movementDetails.title,
                onValueChange = { onTitleChanged(it) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            GenericField(
                label = R.string.add_movement_description_label,
                placeholder = R.string.add_movement_description_placeholder,
                value = movementDetails.description,
                onValueChange = { onDescriptionChanged(it) },
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            GenericField(
                label = R.string.add_movement_amount_label,
                placeholder = R.string.add_movement_amount_placeholder,
                value = movementDetails.amount,
                onValueChange = { onAmountChanged(it) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                supportingText = {
                    CashSupportingText(
                        fund = senderFund,
                        selectedFund = movementDetails.receiverFund,
                        movementType = movementDetails.type,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.EuroSymbol,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Spacer(Modifier.weight(1f))
            BucksFilledButton(
                text = R.string.confirm,
                enabled = isMovementValid,
                onClick = onConfirmButtonClicked,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Reference(
    fund: Fund,
    modifier: Modifier = Modifier
) {
    SectionMovement(
        header = { HeaderText(text = R.string.add_movement_ref_label) },
        field = { ReferenceField(fund = fund) },
        modifier = modifier
    )
}

@Composable
fun SecondaryReference(
    amountType: MovementType,
    allFunds: List<Fund>,
    receiverFund: Fund?,
    value: String,
    onValueChange: (String) -> Unit,
    onItemClick: (Fund) -> Unit,
    modifier: Modifier = Modifier
) {
    var referenceLabel = R.string.add_movement_sender_label
    var referencePlaceholder = R.string.add_movement_sender_placeholder
    when (amountType) {
        MovementType.In -> {
            referenceLabel = R.string.add_movement_sender_label
            referencePlaceholder = R.string.add_movement_sender_placeholder
        }
        MovementType.Out -> {
            referenceLabel = R.string.add_movement_receiver_label
            referencePlaceholder = R.string.add_movement_receiver_placeholder
        }
    }

    SectionMovement(
        header = {
            HeaderText(text = referenceLabel)
        },
        field = {
            AutoCompleteTextView(
                suggestions = allFunds,
                value = value,
                onValueChange = onValueChange,
                onItemClick = onItemClick,
                placeholder = { FieldInputPlaceholder(text = referencePlaceholder) },
                trailingIcon = {
                    if (receiverFund != null) {
                        BucksFundIcon(
                            fund = receiverFund,
                            containerSize = 24.dp,
                            contentSize = 18.dp,
                            cornerSize = 4.dp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                },
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericField(
    @StringRes label: Int,
    @StringRes placeholder: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    supportingText: @Composable () -> Unit = { },
    trailingIcon: @Composable () -> Unit = { },
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
) {
    SectionMovement(
        header = { HeaderText(text = label) },
        field = {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { FieldInputPlaceholder(text = placeholder) },
                singleLine = singleLine,
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                supportingText = supportingText,
                trailingIcon = trailingIcon,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = modifier
                    .fillMaxWidth()
            )
        },
    )
}

@Composable
fun SectionMovement(
    header: @Composable () -> Unit,
    field: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        header()
        Spacer(Modifier.height(16.dp))
        field()
    }
}

@Composable
fun ReferenceField(
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
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(id = fund.type.id),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AutoCompleteTextView(
    suggestions: List<Fund>,
    value: String,
    onValueChange: (String) -> Unit,
    onItemClick: (Fund) -> Unit,
    placeholder: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazylistState = rememberLazyListState()
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LazyColumn(
        state = lazylistState,
        modifier = modifier
            .heightIn(max = TextFieldDefaults.MinHeight * 4)
    ) {
        stickyHeader {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder,
                singleLine = true,
                trailingIcon = trailingIcon,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                modifier = Modifier
                    .onFocusChanged { focusState ->
                        expanded = focusState.isFocused
                    }
                    .fillMaxWidth()
            )
        }
        if (suggestions.isNotEmpty() && expanded) {
            items(suggestions.filter { it.title.contains(value, true) }) { suggestion ->
                AutoCompleteItem(
                    item = suggestion,
                    onItemClick = { fund ->
                        onItemClick(fund)
                    }
                )
            }
        }
    }
}

@Composable
fun AutoCompleteItem(
    item: Fund,
    onItemClick: (Fund) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable {
                focusManager.moveFocus(FocusDirection.Down)
                onItemClick(item)
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = stringResource(id = item.type.id),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(Modifier.width(16.dp))
        BucksFundIcon(
            fund = item,
            containerSize = 24.dp,
            contentSize = 18.dp,
            cornerSize = 4.dp,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InsertMovementScreenContentCompactPreview() {
    BucksTheme {
        InsertMovementScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            senderFund = mockPrepaid,
            funds = mockFunds,
            isMovementValid = true,
            movementDetails = MovementDetails(
                sourceFund = mockPrepaid,
                receiverFund = null,
                receiverFundTitle = "ciao",
                title = "titolo",
                description = "descrizione",
                type = MovementType.In,
                amount = "120",
                amountValue = 125.0,
                date = System.currentTimeMillis()
            ),
            onMovementTypeSelected = {},
            onReceiverFundChanged = {},
            onReceiverFundSelected = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onAmountChanged = {},
            onConfirmButtonClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun InsertMovementScreenContentMediumPreview() {
    BucksTheme {
        InsertMovementScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            senderFund = mockPrepaid,
            funds = mockFunds,
            isMovementValid = true,
            movementDetails = MovementDetails(
                sourceFund = mockPrepaid,
                receiverFund = null,
                receiverFundTitle = "ciao",
                title = "titolo",
                description = "descrizione",
                type = MovementType.In,
                amount = "120",
                amountValue = 125.0,
                date = System.currentTimeMillis()
            ),
            onMovementTypeSelected = {},
            onReceiverFundChanged = {},
            onReceiverFundSelected = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onAmountChanged = {},
            onConfirmButtonClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
fun InsertMovementScreenContentExpandedPreview() {
    BucksTheme {
        InsertMovementScreenContent(
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            senderFund = mockPrepaid,
            funds = mockFunds,
            isMovementValid = true,
            movementDetails = MovementDetails(
                sourceFund = mockPrepaid,
                receiverFund = null,
                receiverFundTitle = "ciao",
                title = "titolo",
                description = "descrizione",
                type = MovementType.In,
                amount = "120",
                amountValue = 125.0,
                date = System.currentTimeMillis()
            ),
            onMovementTypeSelected = {},
            onReceiverFundChanged = {},
            onReceiverFundSelected = {},
            onTitleChanged = {},
            onDescriptionChanged = {},
            onAmountChanged = {},
            onConfirmButtonClicked = {}
        )
    }
}

