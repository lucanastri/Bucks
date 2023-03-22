package com.kizune.bucks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R
import com.kizune.bucks.ui.navigation.NavigationUIEvents

@Composable
fun EmptyCardsScreen(
    navigationUIHandler: NavigationUIEvents,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_creditcard_off),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.cards_empty_title),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = stringResource(id = R.string.cards_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        BucksFilledButton(
            text = R.string.cards_empty_button,
            onClick = { navigationUIHandler.onPlaceholderClicked() },
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
}

@Composable
fun EmptyReportScreen(
    onInsertFundClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_report_off),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.report_empty_title),
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = stringResource(id = R.string.report_empty_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        BucksFilledButton(
            text = R.string.cards_empty_button,
            onClick = onInsertFundClicked,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }

}

@Composable
fun EmptyMovementsScreen(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.movements_empty_title),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}