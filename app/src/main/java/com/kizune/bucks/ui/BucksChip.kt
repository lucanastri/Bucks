package com.kizune.bucks.ui

import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BucksChip(
    item: Int,
    index: Int,
    selected: Boolean,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        label = { ChipText(text = item) },
        selected = selected,
        onClick = { onChipSelected(index) },
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    )
}

@Composable
fun ChipText(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = text),
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}