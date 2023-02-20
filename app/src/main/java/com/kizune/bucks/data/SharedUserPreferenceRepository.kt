package com.kizune.bucks.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.kizune.bucks.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

enum class Currency(
    @StringRes val title: Int,
    @StringRes val abbreviation: Int,
    val symbol: String,
    val icon: ImageVector
) {
    Euro(
        R.string.currency_euro,
        R.string.currency_euro_abbreviation,
        "€",
        Icons.Rounded.EuroSymbol
    ),
    Dollar(
        R.string.currency_dollar,
        R.string.currency_dollar_abbreviation,
        "$",
        Icons.Rounded.AttachMoney
    ),
    Pound(
        R.string.currency_pound,
        R.string.currency_pound_abbreviation,
        "£",
        Icons.Rounded.CurrencyPound
    ),
    Franc(
        R.string.currency_franc,
        R.string.currency_franc_abbreviation,
        "₣",
        Icons.Rounded.CurrencyFranc
    ),
    Yen(R.string.currency_yen, R.string.currency_yen_abbreviation, "¥", Icons.Rounded.CurrencyYen),
    Yuan(
        R.string.currency_yuan,
        R.string.currency_yuan_abbreviation,
        "元",
        Icons.Rounded.CurrencyYuan
    )
}

enum class DateFormat(
    val date: String,
    val time: String,
) {
    FormatA("dd MMM yyyy", "HH:mm"),
    FormatAB("dd MMM yyyy", "hh:mm aa"),
    FormatB("dd/MM/yy", "HH:mm"),
    FormatBB("dd/MM/yy", "hh:mm aa"),
    FormatC("dd/MM/yyyy", "HH:mm"),
    FormatCB("dd/MM/yyyy", "hh:mm aa"),
    FormatD("yyyy/MM/dd", "HH:mm"),
    FormatDB("yyyy/MM/dd", "hh:mm aa"),
    FormatE("yyyy MMM dd", "HH:mm"),
    FormatEB("yyyy MMM dd", "hh:mm aa"),
}

enum class ReportFilter(
    @StringRes val id: Int,
    val timeInMillis: Long,
) {
    LAST_DAY(R.string.report_menu_item_1, TimeUnit.DAYS.toMillis(1)),
    LAST_WEEK(R.string.report_menu_item_2, TimeUnit.DAYS.toMillis(7)),
    LAST_MONTH(R.string.report_menu_item_3, TimeUnit.DAYS.toMillis(28)),
    LAST_YEAR(R.string.report_menu_item_4, TimeUnit.DAYS.toMillis(365)),
    EVER(R.string.report_menu_item_5, TimeUnit.DAYS.toMillis(Long.MAX_VALUE))
}

class SharedUserPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val ONBOARDING_KEY = booleanPreferencesKey("onboarding")
        val CURRENCY_KEY = intPreferencesKey("currency")
        val DATEFORMAT_KEY = intPreferencesKey("dateformat")
        val REPORTFILTER_KEY = intPreferencesKey("report_filter")

        val BACKUP_CREATION = longPreferencesKey("backup_creation")
        val BACKUP_RECOVER = longPreferencesKey("backup_recover")
    }

    suspend fun setOnBoarding(flag: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_KEY] = flag
        }
    }

    suspend fun setCurrency(flag: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = flag
        }
    }

    suspend fun setDateFormat(flag: Int) {
        dataStore.edit { preferences ->
            preferences[DATEFORMAT_KEY] = flag
        }
    }

    suspend fun setBackupCreation(flag: Long) {
        dataStore.edit { preferences ->
            preferences[BACKUP_CREATION] = flag
        }
    }

    suspend fun setBackupRecover(flag: Long) {
        dataStore.edit { preferences ->
            preferences[BACKUP_RECOVER] = flag
        }
    }

    suspend fun setReportFilter(flag: Int) {
        dataStore.edit { preferences ->
            preferences[REPORTFILTER_KEY] = flag
        }
    }

    val preference: Flow<BucksPreference> = dataStore.data
        .catch { }
        .map { preferences ->
            BucksPreference(
                onBoarding = preferences[ONBOARDING_KEY] ?: false,
                currency = preferences[CURRENCY_KEY] ?: 0,
                dateFormat = preferences[DATEFORMAT_KEY] ?: 0,
                reportfilter = preferences[REPORTFILTER_KEY] ?: 0,
                backupCreation = preferences[BACKUP_CREATION] ?: 0,
                backupRecover = preferences[BACKUP_RECOVER] ?: 0
            )
        }
}

data class BucksPreference(
    val onBoarding: Boolean,
    val currency: Int,
    val dateFormat: Int,
    val reportfilter: Int,
    val backupCreation: Long,
    val backupRecover: Long,
)