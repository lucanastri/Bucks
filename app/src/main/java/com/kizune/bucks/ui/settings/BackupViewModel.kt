package com.kizune.bucks.ui.settings

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kizune.bucks.R
import com.kizune.bucks.data.DatabaseRepository
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.Movement
import com.kizune.bucks.ui.BucksSnackbarVisuals
import com.kizune.bucks.ui.SnackbarType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

const val BACKUP_DELAY = 2_000L

@HiltViewModel
@Singleton
class BackupViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val _showBackupCreationDialog = MutableStateFlow(false)
    val showBackupCreationDialog: StateFlow<Boolean> = _showBackupCreationDialog.asStateFlow()

    private val _showBackupRecoverDialog = MutableStateFlow(false)
    val showBackupRecoverDialog: StateFlow<Boolean> = _showBackupRecoverDialog.asStateFlow()

    fun showBackupCreationDialog() {
        _showBackupCreationDialog.value = true
    }

    fun showBackupRecoverDialog() {
        _showBackupRecoverDialog.value = true
    }

    private fun hideBackupCreationDialog() {
        _showBackupCreationDialog.value = false
    }

    private fun hideBackupRecoverDialog() {
        _showBackupRecoverDialog.value = false
    }

    suspend fun insertFund(fund: Fund) {
        databaseRepository.insertFund(fund)
    }

    suspend fun insertMovement(movement: Movement) {
        databaseRepository.insertMovement(movement)
    }

    fun serializeDatabase(
        context: Context,
        snackbarHostState: SnackbarHostState,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val funds = databaseRepository.getFunds()
            val movements = databaseRepository.getMovements()

            if (funds.isEmpty()) {
                delay(BACKUP_DELAY)
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.backup_creation_empty),
                    type = SnackbarType.MESSAGE
                )
            } else {
                val serializedFunds = Json.encodeToString(funds)
                val serializedMovements = Json.encodeToString(movements)

                val storagePath = Environment.getExternalStorageDirectory()
                val dir = File(storagePath, "Bucks Backup")
                dir.mkdir()

                try {
                    writeToFile(serializedFunds, File(dir, "fund.txt"))
                    writeToFile(serializedMovements, File(dir, "movement.txt"))
                    delay(BACKUP_DELAY)
                    onSuccess()
                    showSnackbar(
                        snackbarHostState = snackbarHostState,
                        message = context.getString(R.string.backup_creation_success),
                        type = SnackbarType.SUCCESS
                    )
                } catch (e: Exception) {
                    Log.d("MyTag", "ThrowingException: $e")
                    showSnackbar(
                        snackbarHostState = snackbarHostState,
                        message = context.getString(R.string.backup_creation_error),
                        type = SnackbarType.ERROR
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    suspend fun showSnackbar(
        snackbarHostState: SnackbarHostState,
        message: String,
        type: SnackbarType
    ) {
        hideBackupCreationDialog()
        hideBackupRecoverDialog()
        snackbarHostState.showSnackbar(
            BucksSnackbarVisuals(
                message = message,
                type = type
            )
        )
    }

    private fun writeToFile(
        json: String,
        destinationFile: File
    ) {
        destinationFile.createNewFile()
        destinationFile.printWriter().use { out ->
            out.println(json)
        }
    }

    fun deserializeDatabase(
        context: Context,
        snackbarHostState: SnackbarHostState,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            val storagePath = Environment.getExternalStorageDirectory()

            val dir = File(storagePath, "Bucks Backup")
            val sourceFundFile = File(dir, "fund.txt")
            val sourceMovementFile = File(dir, "movement.txt")

            if (dir.exists() && sourceFundFile.exists() && sourceMovementFile.exists()) {
                val fundsFromFile = readFromFile<Fund>(sourceFundFile)
                val movementsFromFile = readFromFile<Movement>(sourceMovementFile)

                try {
                    fundsFromFile.forEach { item ->
                        databaseRepository.insertFund(item)
                    }
                    movementsFromFile.forEach { item ->
                        databaseRepository.insertMovement(item)
                    }
                    delay(BACKUP_DELAY)
                    onSuccess()
                    showSnackbar(
                        snackbarHostState = snackbarHostState,
                        message = context.getString(R.string.backup_recover_success),
                        type = SnackbarType.SUCCESS
                    )
                } catch (e: Exception) {
                    Log.d("MyTag", "$e")
                    showSnackbar(
                        snackbarHostState = snackbarHostState,
                        message = context.getString(R.string.backup_recover_error),
                        type = SnackbarType.ERROR
                    )
                }
            } else {
                delay(BACKUP_DELAY)
                showSnackbar(
                    snackbarHostState = snackbarHostState,
                    message = context.getString(R.string.backup_recover_empty),
                    type = SnackbarType.MESSAGE
                )
            }
        }
    }

    private inline fun <reified T> readFromFile(
        sourceFile: File
    ): List<T> {
        val text: String
        try {
            sourceFile.reader().use { input ->
                text = input.readText()
            }
            return Json.decodeFromString(text)
        } catch (e: Exception) {
            Log.d("MyTag", "ThrowingException: $e")
        }
        return listOf()
    }
}