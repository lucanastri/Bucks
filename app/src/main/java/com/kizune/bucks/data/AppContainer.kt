package com.kizune.bucks.data

import android.content.Context

interface AppContainer {
    val databaseRepository: DatabaseRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val databaseRepository: DatabaseRepository by lazy {
        FundRepository(BucksDatabase.getInstance(context).fundDao())
    }
}