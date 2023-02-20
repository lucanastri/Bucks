package com.kizune.bucks.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R

@Composable
fun BucksTopBar(
    title: String,
    onBackClicked: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )
    ) {
        IconButton(
            onClick = onBackClicked
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(R.string.close),
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        PageLabel(
            title = title,
            modifier = Modifier.weight(1f)
        )
    }
}