package com.kizune.bucks.ui.checking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.*
import com.kizune.bucks.ui.dialog.DeleteFundDialog
import com.kizune.bucks.ui.dialog.DialogsViewModel
import com.kizune.bucks.ui.navigation.Destination
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.utils.*
import kotlinx.coroutines.launch

object CheckingDestination : Destination {
    override val route = "Checking"
    const val fundIDArg = "fundID"
    val routeWithArgs = "$route/{$fundIDArg}"
}

val checkingTabItems = listOf(
    R.string.tab_history,
    R.string.tab_in,
    R.string.tab_out
)

@Composable
fun CheckingScreen(
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    checkingViewModel: CheckingViewModel = viewModel(factory = AppViewModelProvider.Factory),
    dialogsViewModel: DialogsViewModel = viewModel()
) {
    val uiState by checkingViewModel.checkingUiState.collectAsState()
    val fund = uiState.fund

    val coroutineScope = rememberCoroutineScope()
    val onDeleteClicked: () -> Unit = {
        dialogsViewModel.showDeleteFundDialog = true
    }

    val onDeleteConfirmClicked: () -> Unit = {
        dialogsViewModel.deleteFundConfirm(
            coroutineScope = coroutineScope,
            navigationUIHandler = navigationUIHandler,
            viewModel = checkingViewModel
        )
    }

    if (dialogsViewModel.showDeleteFundDialog) {
        DeleteFundDialog(
            onDeleteConfirmClicked = onDeleteConfirmClicked
        )
    }

    when (uiState.result) {
        CheckingUiStateResult.Loading -> {
            Box(modifier = modifier.background(MaterialTheme.colorScheme.background))
        }
        CheckingUiStateResult.Success -> {
            CheckingScreenContent(
                onDeleteClicked = onDeleteClicked,
                fund = fund,
                navigationUIHandler = navigationUIHandler,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckingScreenContent(
    onDeleteClicked: () -> Unit,
    fund: FundWithMovements,
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    showTopBar: Boolean = true,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            if (showTopBar)
                BucksTopBar(
                    title = fund.fund.title,
                    imageVector = Icons.Rounded.ArrowBack,
                    onBackClicked = { navigationUIHandler.onNavigateUp() },
                )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp(),
                enter = enterTransition(250),
                exit = exitTransition(200)
            ) {
                BucksFloatingActionButton(onFabClicked = {
                    navigationUIHandler.onInsertMovementClicked(fund.fund)
                })
            }
        },
        modifier = modifier
    ) { padding ->
        CheckingList(
            onDeleteClicked = onDeleteClicked,
            fund = fund,
            listState = listState,
            navigationUIHandler = navigationUIHandler,
            modifier = Modifier.padding(padding)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CheckingList(
    onDeleteClicked: () -> Unit,
    fund: FundWithMovements,
    navigationUIHandler: NavigationUIHandler,
    listState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val movements = fund.movementsOut.plus(fund.movementsIn).sortedByDescending { it.date }
    val movementsIn = fund.movementsIn.sortedByDescending { it.date }
    val movementsOut = fund.movementsOut.sortedByDescending { it.date }

    val movementsGroup = movements.groupBy { it.date.convertToFormattedDate(0) }
    val movementsInGroup =
        movementsIn.groupBy { it.date.convertToFormattedDate(0) }
    val movementsOutGroup =
        movementsOut.groupBy { it.date.convertToFormattedDate(0) }

    var selectedTabItem by remember { mutableStateOf(0) }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        item {
            BucksCard(
                fund = fund.fund,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(24.dp))
            CheckingButtons(
                onDeleteClicked = onDeleteClicked,
                onEditClicked = {
                    navigationUIHandler.onEditClicked(fund.fund)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(24.dp))
        }
        stickyHeader {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                TabLayout(
                    selectedTabItem = selectedTabItem,
                    tabItems = checkingTabItems,
                    onTabClick = { index ->
                        selectedTabItem = index
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(6.dp))
                Divider(color = MaterialTheme.colorScheme.outline)
            }
        }
        val items = when (selectedTabItem) {
            0 -> movementsGroup
            1 -> movementsInGroup
            else -> movementsOutGroup
        }
        item {
            AnimatedVisibility(
                visible = items.isEmpty(),
                enter = enterTransition(250),
                exit = exitTransition(0),
                modifier = Modifier.padding(16.dp)
            ) {
                EmptyMovementsScreen()
            }
        }
        items.forEach { entry ->
            itemsIndexed(
                items = entry.value,
                key = { _, movement -> movement.movementID }
            ) { index, item ->
                Column(
                    modifier = Modifier.animateItemPlacement()
                ) {
                    if (index == 0) {
                        MovementHeader(
                            movement = entry.value[0],
                            modifier = Modifier
                        )
                    }
                    SwipeToDismissItem(
                        item = item,
                        fund = fund,
                        modifier = Modifier
                    )
                    if (index + 1 != entry.value.size) {
                        Divider(color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckingButtons(
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        BucksOutlinedButton(
            text = R.string.delete,
            onClick = onDeleteClicked,
            modifier = Modifier.weight(0.5f)
        )
        BucksFilledButton(
            text = R.string.edit,
            onClick = onEditClicked,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun TabLayout(
    selectedTabItem: Int,
    tabItems: List<Int>,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val indicatorWidths = remember {
        val indicatorWidthsList = mutableStateListOf<Dp>()
        repeat(tabItems.size) {
            indicatorWidthsList.add(0.dp)
        }
        indicatorWidthsList
    }

    TabRow(
        selectedTabIndex = selectedTabItem,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .customTabIndicatorOffset(
                        currentTabPosition = tabPositions[selectedTabItem],
                        tabWidth = indicatorWidths[selectedTabItem]
                    )
                    .height(4.dp)
                    .clip(RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
            )
        },
        divider = {},
        tabs = {
            tabItems.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabItem == index,
                    onClick = { onTabClick(index) },
                    text = {
                        Text(
                            text = stringResource(id = title),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedTabItem == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onBackground,
                            onTextLayout = { result ->
                                indicatorWidths[index] = with(density) { result.size.width.toDp() }
                            },
                        )
                    },
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun MovementHeader(
    movement: Movement,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 8.dp, horizontal = 32.dp)
    ) {
        DateText(
            value = movement.date,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun MovementCard(
    fundID: Long,
    movement: Movement,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RectangleShape,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    horizontal = 32.dp,
                    vertical = 16.dp
                )
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movement.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = movement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(16.dp))
            CashText(
                value = if (fundID == movement.fundOutID) -movement.amount else movement.amount,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissItem(
    item: Movement,
    fund: FundWithMovements,
    modifier: Modifier = Modifier,
    viewModel: CheckingViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberDismissState(
        confirmStateChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    coroutineScope.launch {
                        viewModel.deleteMovement(item)
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    )
    val directions: Set<DismissDirection> = setOf(DismissDirection.EndToStart)
    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        background = { SwipeToDismissBackground() },
        directions = directions,
        dismissThresholds = {
            FractionalThreshold(0.7f)
        },
        dismissContent = {
            MovementCard(
                fundID = fund.fund.fundID,
                movement = item,
            )
        }
    )
}

@Composable
fun SwipeToDismissBackground(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.padding(end = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Delete,
                contentDescription = stringResource(id = R.string.delete),
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(id = R.string.delete),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckingScreenContentCompactPreview() {
    BucksTheme {
        CheckingScreenContent(
            onDeleteClicked = { },
            fund = mockPrepaidWithMovements,
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun CheckingContentMediumPreview() {
    BucksTheme {
        CheckingScreenContent(
            onDeleteClicked = { },
            fund = mockPrepaidWithMovements,
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 1200)
@Composable
fun CheckingContentExpandedPreview() {
    BucksTheme {
        CheckingScreenContent(
            onDeleteClicked = { },
            fund = mockPrepaidWithMovements,
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
        )
    }
}

