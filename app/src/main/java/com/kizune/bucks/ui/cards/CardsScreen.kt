package com.kizune.bucks.ui.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.R
import com.kizune.bucks.model.*
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.BucksCard
import com.kizune.bucks.ui.BucksFloatingActionButton
import com.kizune.bucks.ui.EmptyCardsScreen
import com.kizune.bucks.ui.navigation.NavigationUIEvents
import com.kizune.bucks.ui.navigation.NavigationUIHandler
import com.kizune.bucks.utils.*

@Composable
fun CardsScreen(
    onFabClicked: () -> Unit,
    navigationUIHandler: NavigationUIHandler,
    modifier: Modifier = Modifier,
    cardsViewModel: CardsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val cardsUiState by cardsViewModel.cardsUiState.collectAsState()

    Crossfade(targetState = cardsUiState.result) { result ->
        when (result) {
            CardsUiStateResult.Success -> {
                CardsScreenContent(
                    onFabClicked = onFabClicked,
                    navigationUIHandler = navigationUIHandler,
                    funds = cardsUiState.fundList,
                    modifier = modifier
                )
            }
            CardsUiStateResult.Loading -> {}
            CardsUiStateResult.Empty -> {
                EmptyCardsScreen(navigationUIHandler = navigationUIHandler)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreenContent(
    onFabClicked: () -> Unit,
    navigationUIHandler: NavigationUIEvents,
    funds: List<Fund>,
    modifier: Modifier = Modifier
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp(),
                enter = enterTransition(250),
                exit = exitTransition(200)
            ) {
                BucksFloatingActionButton(onFabClicked = onFabClicked)
            }
        },
        topBar = {
            SearchView(
                searchQuery = searchQuery,
                onValueChange = { searchQuery = it },
                onCloseSearch = { searchQuery = "" },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.background)
            )
        },
        modifier = modifier
    ) { padding ->
        CardsList(
            funds = funds,
            searchQuery = searchQuery,
            onCardClicked = { navigationUIHandler.onCardClicked(it) },
            listState = listState,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun CardsList(
    funds: List<Fund>,
    searchQuery: String,
    listState: LazyListState,
    onCardClicked: (Fund) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        items(funds.filter { it.title.uppercase().contains(searchQuery.uppercase()) }) { fund ->
            BucksCard(
                fund = fund,
                onCardClick = onCardClicked,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchView(
    searchQuery: String,
    onValueChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        TextField(
            value = searchQuery,
            onValueChange = { value ->
                onValueChange(value)
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.cards_search_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank(),
                    enter = enterTransition(400),
                    exit = exitTransition(250)
                ) {
                    IconButton(
                        onClick = onCloseSearch,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(id = R.string.cards_search_close_description),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(50),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(40.dp)
                .padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardsScreenContentCompactPreview() {
    BucksTheme {
        CardsScreenContent(
            onFabClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            funds = mockFunds
        )
    }
}

@Preview(showBackground = true, widthDp = 700 - 80)
@Composable
fun CardsScreenContentMediumPreview() {
    BucksTheme {
        CardsScreenContent(
            onFabClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            funds = mockFunds
        )
    }
}

@Preview(showBackground = true, widthDp = 1200 - 300, heightDp = 800)
@Composable
fun CardsScreenContentExpandedPreview() {
    BucksTheme {
        CardsScreenContent(
            onFabClicked = { },
            navigationUIHandler = NavigationUIHandler(
                parentNavController = rememberNavController(),
                childNavController = rememberNavController(),
            ),
            funds = mockFunds
        )
    }
}
