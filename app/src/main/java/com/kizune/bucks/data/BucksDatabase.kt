package com.kizune.bucks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kizune.bucks.model.Fund
import com.kizune.bucks.model.FundDao
import com.kizune.bucks.model.Movement

@Database(
    entities = [Fund::class, Movement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BucksConverters::class)
abstract class BucksDatabase : RoomDatabase() {
    abstract fun fundDao(): FundDao

    companion object {
        @Volatile
        private var INSTANCE: BucksDatabase? = null

        fun getInstance(context: Context): BucksDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                BucksDatabase::class.java,
                "bucks-database"
            ).fallbackToDestructiveMigration().build()

    }
}