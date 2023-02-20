package com.kizune.bucks.ui.report

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.CallMade
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.data.ReportFilter
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundWithMovements
import com.kizune.bucks.model.mockFundWithMovements
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.*
import com.kizune.bucks.ui.checking.MovementCard
import com.kizune.bucks.ui.checking.MovementHeader
import com.kizune.bucks.ui.checking.TabLayout
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.ui.settings.SettingsUiState
import com.kizune.bucks.ui.settings.SettingsViewModel
import com.kizune.bucks.utils.*
import kotlin.math.max

val reportTabItems = listOf(
    R.string.nav_cards,
    R.string.nav_movements
)

@Composable
fun ReportScreen(
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val reportUiState by reportViewModel.reportUiState.collectAsState()
    val settingsUiState by settingsViewModel.preferenceUiState.collectAsState()

    val onMenuItemClick: (ReportFilter) -> Unit = { filter ->
        settingsViewModel.setReportFilter(filter.ordinal)
    }

    Crossfade(targetState = reportUiState.result) { state ->
        when (state) {
            ReportUiStateResult.Success -> {
                ReportScreenContent(
                    reportUiState = reportUiState,
                    settingsUiState = settingsUiState,
                    onCardClicked = { navigationUIHandler.onCardClicked(it) },
                    onMenuItemClick = onMenuItemClick,
                    modifier = modifier
                )
            }
            ReportUiStateResult.Loading -> {}
            ReportUiStateResult.Empty -> {
                EmptyReportScreen(onInsertFundClicked = { navigationUIHandler.onPlaceholderClicked() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReportScreenContent(
    settingsUiState: SettingsUiState,
    reportUiState: ReportUiState,
    onMenuItemClick: (ReportFilter) -> Unit,
    onCardClicked: (Fund) -> Unit,
    modifier: Modifier = Modifier,
) {
    val reportFilter = settingsUiState.reportFilterPreference
    val timeFilter = enumValues<ReportFilter>()[reportFilter]
    val list = reportUiState.list

    val enterValue = list.sumOf { completeFund ->
        completeFund.movementsIn
            .filter { movement ->
                movement.fundOutID == null
                        && movement.date.isInRange(timeFilter.timeInMillis)
            }
            .sumOf { movement ->
                movement.amount
            }
    }

    val exitValue = list.sumOf { completeFund ->
        completeFund.movementsOut
            .filter { movement ->
                movement.fundInID == null
                        && movement.date.isInRange(timeFilter.timeInMillis)
            }
            .sumOf { movement ->
                movement.amount
            }
    }

    val totalMovements = list
        .map { completeFund ->
            completeFund.movementsIn.plus(completeFund.movementsOut)
                .sortedByDescending { movement ->
                    movement.date
                }.filter { movement ->
                    (movement.fundInID == null || movement.fundOutID == null)
                            && movement.date.isInRange(timeFilter.timeInMillis)
                }
        }.flatten().groupBy { it.date.convertToFormattedDate(0) }


    val totalValue = enterValue - exitValue

    var selectedTabItem by remember { mutableStateOf(0) }
    val cardScrollState = rememberLazyListState()
    val movementScrollState = rememberLazyListState()
    val listState = if (selectedTabItem == 0) cardScrollState else movementScrollState

    Scaffold(
        topBar = {
            ReportTopBar(
                selectedMenuItem = timeFilter,
                onMenuItemClick = onMenuItemClick
            )
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                HeaderList(
                    totalValue = totalValue,
                    enterValue = enterValue,
                    exitValue = exitValue,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
            stickyHeader {
                TabLayout(
                    selectedTabItem = selectedTabItem,
                    tabItems = reportTabItems,
                    onTabClick = { index ->
                        selectedTabItem = index
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                )
            }
            when (selectedTabItem) {
                0 -> {
                    itemsIndexed(list) { index, item ->
                        if (index != 0) Spacer(Modifier.height(16.dp))
                        ReportCardItem(
                            completeFund = item,
                            selectedMenuItem = timeFilter,
                            onCardClicked = onCardClicked,
                            modifier = modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
                1 -> {
                    item {
                        AnimatedVisibility(
                            visible = totalMovements.isEmpty(),
                            enter = enterTransition(250),
                            exit = exitTransition(0),
                        ) {
                            EmptyMovementsScreen()
                        }
                    }
                    totalMovements.forEach { entry ->
                        item {
                            MovementHeader(
                                movement = entry.value[0],
                            )
                        }
                        itemsIndexed(entry.value) { index, item ->
                            val fundID = item.fundInID ?: item.fundOutID
                            MovementCard(
                                fundID = fundID ?: 0,
                                movement = item,
                            )
                            if (index + 1 != entry.value.size) {
                                Divider(color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun ReportTopBar(
    selectedMenuItem: ReportFilter,
    onMenuItemClick: (ReportFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedMenu by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PageLabel(
            title = stringResource(id = R.string.report_title),
            modifier = Modifier.weight(1f)
        )
        Box {
            BucksTextButton(
                text = selectedMenuItem.id,
                onClick = { expandedMenu = true },
                contentColor = MaterialTheme.colorScheme.onBackground
            )
            ReportDropDownMenu(
                expanded = expandedMenu,
                onDismissRequest = { expandedMenu = !expandedMenu },
                onSelectedItem = { index ->
                    expandedMenu = !expandedMenu
                    onMenuItemClick(enumValues<ReportFilter>()[index])
                },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
        }
    }
}

@Composable
fun ReportDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSelectedItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        content = {
            val menuItems = enumValues<ReportFilter>()
            menuItems.forEachIndexed { index, filter ->
                DropdownMenuItem(
                    text = { ReportDropDownMenuText(text = filter.id) },
                    onClick = { onSelectedItem(index) }
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun ReportDropDownMenuText(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

@Composable
fun HeaderList(
    totalValue: Double,
    enterValue: Double,
    exitValue: Double,
    modifier: Modifier = Modifier
) {
    val maxValue = max(exitValue, enterValue)
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        HeaderItem(
            title = R.string.report_header_total,
            value = totalValue,
            normalizedValue = totalValue.normalizeData(maxValue),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        HeaderItem(
            title = R.string.tab_in,
            value = enterValue,
            normalizedValue = enterValue.normalizeData(maxValue),
            color = MaterialTheme.colorScheme.tertiaryContainer,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        HeaderItem(
            title = R.string.tab_out,
            value = exitValue,
            normalizedValue = exitValue.normalizeData(maxValue),
            color = MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
fun HeaderItem(
    @StringRes title: Int,
    value: Double,
    normalizedValue: Double,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HeaderLabel(
            title = title,
            value = value,
        )
        Spacer(Modifier.height(8.dp))
        HeaderProgressBar(
            normalizedValue = normalizedValue,
            color = color,
            trackColor = trackColor
        )
    }
}

@Composable
fun HeaderLabel(
    @StringRes title: Int,
    value: Double,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(16.dp))
        CashText(
            value = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
        )
    }
}

@Composable
fun HeaderProgressBar(
    normalizedValue: Double,
    color: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
) {
    val animatedValue = animateFloatAsState(
        targetValue = normalizedValue.toFloat(),
        animationSpec = tween(durationMillis = 800)
    )
    LinearProgressIndicator(
        progress = animatedValue.value,
        color = color,
        trackColor = trackColor,
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCardItem(
    completeFund: FundWithMovements,
    selectedMenuItem: ReportFilter,
    onCardClicked: (Fund) -> Unit,
    modifier: Modifier = Modifier
) {
    val enterValue = completeFund.movementsIn
        .filter { movement ->
            movement.date.isInRange(selectedMenuItem.timeInMillis)
        }.sumOf { movement ->
            movement.amount
        }
    val exitValue = completeFund.movementsOut
        .filter { movement ->
            movement.date.isInRange(selectedMenuItem.timeInMillis)
        }.sumOf { movement ->
            movement.amount
        }

    val totalValue = enterValue - exitValue
    Card(
        onClick = { onCardClicked(completeFund.fund) },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = completeFund.fund.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(id = completeFund.fund.type.id),
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                BucksFundIcon(fund = completeFund.fund)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ReportDetailItem(
                    icon = Icons.Rounded.AccountBalanceWallet,
                    value = totalValue,
                    contentDescription = R.string.report_header_total
                )
                ReportDetailItem(
                    icon = Icons.Rounded.CallReceived,
                    value = enterValue,
                    contentDescription = R.string.tab_in
                )
                ReportDetailItem(
                    icon = Icons.Rounded.CallMade,
                    value = exitValue,
                    contentDescription = R.string.tab_out
                )
            }
        }
    }
}

@Composable
fun ReportDetailItem(
    icon: ImageVector,
    value: Double,
    @StringRes contentDescription: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(id = contentDescription)
        )
        CashText(
            value = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReportScreenContentCompactPreview() {
    BucksTheme {
        ReportScreenContent(
            settingsUiState = SettingsUiState(),
            reportUiState = ReportUiState(
                list = mockFundWithMovements,
                result = ReportUiStateResult.Success
            ),
            onMenuItemClick = {},
            onCardClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700 - 80)
@Composable
fun ReportScreenContentMediumPreview() {
    BucksTheme {
        ReportScreenContent(
            settingsUiState = SettingsUiState(),
            reportUiState = ReportUiState(
                list = mockFundWithMovements,
                result = ReportUiStateResult.Success
            ),
            onMenuItemClick = {},
            onCardClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200 - 300, heightDp = 800)
@Composable
fun ReportScreenContentExpandedPreview() {
    BucksTheme {
        ReportScreenContent(
            settingsUiState = SettingsUiState(),
            reportUiState = ReportUiState(
                list = mockFundWithMovements,
                result = ReportUiStateResult.Success
            ),
            onMenuItemClick = {},
            onCardClicked = {}
        )
    }
}