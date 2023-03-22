package com.kizune.bucks.ui

import androidx.annotation.StringRes
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex

@Composable
fun BucksFilledButton(
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier.zIndex(10f)
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun BucksOutlinedButton(
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun BucksTextButton(
    @StringRes text: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = contentColor
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

