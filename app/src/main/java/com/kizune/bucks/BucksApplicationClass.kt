package com.kizune.bucks

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kizune.bucks.data.AppContainer
import com.kizune.bucks.data.AppDataContainer
import com.kizune.bucks.data.SharedUserPreferenceRepository

private const val BUCKS_PREFERENCE_NAME = "bucks_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = BUCKS_PREFERENCE_NAME
)

class BucksApplicationClass : Application() {
    lateinit var container: AppContainer
    lateinit var sharedUserPreferenceRepository: SharedUserPreferenceRepository
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        sharedUserPreferenceRepository = SharedUserPreferenceRepository(dataStore)
    }
}