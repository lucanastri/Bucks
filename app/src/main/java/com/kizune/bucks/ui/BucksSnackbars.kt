package com.kizune.bucks.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R
import com.kizune.bucks.theme.BucksTheme

enum class SnackbarType {
    ERROR,
    MESSAGE,
    SUCCESS
}

class BucksSnackbarVisuals(
    override val message: String,
    val type: SnackbarType,
) : SnackbarVisuals {
    override val withDismissAction: Boolean
        get() = true
    override val actionLabel: String?
        get() = null
    override val duration: SnackbarDuration
        get() = SnackbarDuration.Short
}

@Composable
fun BucksSnackbar(
    message: String,
    type: SnackbarType,
    dismissAction: () -> Unit
) {
    //Surface per rimuovere l'elevation della snackbar
    Surface(
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.extraSmall,
        modifier = Modifier.padding(16.dp)
    ) {
        Snackbar(
            containerColor = when (type) {
                SnackbarType.ERROR -> MaterialTheme.colorScheme.errorContainer
                SnackbarType.MESSAGE -> MaterialTheme.colorScheme.surfaceVariant
                SnackbarType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = when (type) {
                SnackbarType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                SnackbarType.MESSAGE -> MaterialTheme.colorScheme.onSurfaceVariant
                SnackbarType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
            },
            dismissActionContentColor = when (type) {
                SnackbarType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                SnackbarType.MESSAGE -> MaterialTheme.colorScheme.onSurfaceVariant
                SnackbarType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
            },
            shape = MaterialTheme.shapes.extraSmall,
            dismissAction = {
                IconButton(
                    onClick = dismissAction,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = stringResource(id = R.string.clear),
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            content = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
        )
    }
}

@Preview()
@Composable
fun BucksSnackbarPreview() {
    BucksTheme() {
        BucksSnackbar(
            message = "Messaggio snackbar",
            type = SnackbarType.SUCCESS,
            dismissAction = {}
        )
    }
}