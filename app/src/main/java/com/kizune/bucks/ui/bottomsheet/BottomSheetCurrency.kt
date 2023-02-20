package com.kizune.bucks.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R
import com.kizune.bucks.data.Currency

@Composable
fun BottomSheetCurrency(
    selected: Int,
    onItemClick: (Currency) -> Unit,
) {
    Text(
        text = stringResource(id = R.string.settings_currency_subtitle),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn {
        itemsIndexed(enumValues<Currency>().toList()) { index, item ->
            BottomSheetCurrencyItem(
                selected = selected == index,
                currency = item,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun BottomSheetCurrencyItem(
    selected: Boolean,
    currency: Currency,
    onItemClick: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onItemClick(currency) }
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)

    ) {
        BottomSheetIcon(imageVector = currency.icon)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(currency.title),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = stringResource(currency.abbreviation),
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (selected) {
            Spacer(Modifier.width(16.dp))
            BottomSheetIcon(imageVector = Icons.Rounded.Done)
        }
    }
}