package com.kizune.bucks.ui.settings

import android.Manifest
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kizune.bucks.AppViewModelProvider
import com.kizune.bucks.BuildConfig
import com.kizune.bucks.R
import com.kizune.bucks.theme.BucksTheme
import com.kizune.bucks.ui.PageLabel
import com.kizune.bucks.ui.bottomsheet.BottomSheetType
import com.kizune.bucks.ui.dialog.AlertDialog
import com.kizune.bucks.utils.convertToFormattedDateTime

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    onSettingClicked: (BottomSheetType) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    backupViewModel: BackupViewModel = viewModel(factory = AppViewModelProvider.Factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current

    val settingsUiState = settingsViewModel.preferenceUiState.collectAsState().value
    val showBackupCreationDialog by backupViewModel.showBackupCreationDialog.collectAsState()
    val showBackupRecoverDialog by backupViewModel.showBackupRecoverDialog.collectAsState()

    if (showBackupCreationDialog) {
        AlertDialog(
            title = R.string.backup_creation_title,
            subtitle = R.string.backup_creation_subtitle
        )
    }

    if (showBackupRecoverDialog) {
        AlertDialog(
            title = R.string.backup_recover_title,
            subtitle = R.string.backup_recover_subtitle
        )
    }

    val storagePermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    )

    val onCreateBackupClicked: () -> Unit = {
        if (storagePermissionState.allPermissionsGranted) {
            backupViewModel.showBackupCreationDialog()
            backupViewModel.serializeDatabase(
                context = context,
                snackbarHostState = snackbarHostState,
                onSuccess = { settingsViewModel.setBackupCreation(System.currentTimeMillis()) }
            )
        } else {
            storagePermissionState.launchMultiplePermissionRequest()
        }
    }

    val onRecoverBackupClicked: () -> Unit = {
        if (storagePermissionState.allPermissionsGranted) {
            backupViewModel.showBackupRecoverDialog()
            backupViewModel.deserializeDatabase(
                context = context,
                snackbarHostState = snackbarHostState,
                onSuccess = { settingsViewModel.setBackupRecover(System.currentTimeMillis()) }
            )
        } else {
            storagePermissionState.launchMultiplePermissionRequest()
        }
    }

    SettingsScreenContent(
        settingsUiState = settingsUiState,
        onSettingClicked = onSettingClicked,
        onCreateBackupClicked = onCreateBackupClicked,
        onRecoverBackupClicked = onRecoverBackupClicked,
        modifier = modifier
    )
}

@Composable
fun SettingsScreenContent(
    settingsUiState: SettingsUiState,
    onSettingClicked: (BottomSheetType) -> Unit,
    onCreateBackupClicked: () -> Unit,
    onRecoverBackupClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val dateFormatPreference = settingsUiState.dateFormatPreference
    val backupCreationPreference = settingsUiState.backupCreationPreference
    val backupRecoverPreference = settingsUiState.backupRecoverPreference

    Column(
        modifier = modifier
            .verticalScroll(state = scrollState)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PageLabel(
                title = stringResource(id = R.string.nav_settings),
                modifier = Modifier
                    .padding(16.dp)
            )
        }
        SettingsHeader(text = R.string.settings_header_application)
        SettingsItem(
            title = stringResource(id = R.string.settings_currency_title),
            subtitle = stringResource(id = R.string.settings_currency_subtitle),
            onSettingClicked = { onSettingClicked(BottomSheetType.CURRENCY) }
        )
        Spacer(Modifier.height(2.dp))
        SettingsItem(
            title = stringResource(id = R.string.settings_dateformat_title),
            subtitle = stringResource(id = R.string.settings_dateformat_subtitle),
            onSettingClicked = { onSettingClicked(BottomSheetType.DATEFORMAT) }
        )
        Spacer(Modifier.height(2.dp))
        SettingsItem(
            title = stringResource(id = R.string.settings_report_filter_title),
            subtitle = stringResource(id = R.string.settings_report_filter_subtitle),
            onSettingClicked = { onSettingClicked(BottomSheetType.REPORTFILTER) }
        )
        Spacer(Modifier.height(2.dp))
        SettingsHeader(text = R.string.settings_header_backup)
        SettingsItem(
            title = stringResource(id = R.string.settings_backup_title),
            subtitle = if (backupCreationPreference == 0L) {
                stringResource(id = R.string.settings_backup_subtitle)
            } else {
                stringResource(id = R.string.settings_backup_date) + " " +
                        backupCreationPreference.convertToFormattedDateTime(dateFormatPreference)
            },
            onSettingClicked = { onCreateBackupClicked() }
        )
        Spacer(Modifier.height(2.dp))
        SettingsItem(
            title = stringResource(id = R.string.settings_backup_recover_title),
            subtitle = if (backupRecoverPreference == 0L) {
                stringResource(id = R.string.settings_backup_recover_subtitle)
            } else {
                stringResource(id = R.string.settings_backup_recover_date) + " " +
                        backupRecoverPreference.convertToFormattedDateTime(dateFormatPreference)
            },
            onSettingClicked = { onRecoverBackupClicked() }
        )
        Spacer(Modifier.height(30.dp))
        SettingsFooter()
    }
}

@Composable
fun SettingsHeader(
    @StringRes text: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onSettingClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onSettingClicked()
            }
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SettingsFooter(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Bucks ver. ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenContentCompactPreview() {
    BucksTheme {
        SettingsScreenContent(
            settingsUiState = SettingsUiState(),
            onSettingClicked = {},
            onCreateBackupClicked = {},
            onRecoverBackupClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 700 - 80)
@Composable
fun SettingsScreenContentMediumPreview() {
    BucksTheme {
        SettingsScreenContent(
            settingsUiState = SettingsUiState(),
            onSettingClicked = {},
            onCreateBackupClicked = {},
            onRecoverBackupClicked = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 1200 - 300, heightDp = 800)
@Composable
fun SettingsScreenContentExpandedPreview() {
    BucksTheme {
        SettingsScreenContent(
            settingsUiState = SettingsUiState(),
            onSettingClicked = {},
            onCreateBackupClicked = {},
            onRecoverBackupClicked = {}
        )
    }
}



