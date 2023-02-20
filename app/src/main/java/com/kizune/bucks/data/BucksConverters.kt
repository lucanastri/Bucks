package com.kizune.bucks.data

import androidx.room.TypeConverter
import com.kizune.bucks.model.FundNetwork

class BucksConverters {

    @TypeConverter
    fun fromFundNetwork(value: FundNetwork?): Int? {
        return value?.ordinal
    }

    @TypeConverter
    fun toFundNetwork(value: Int?): FundNetwork? {
        return if (value == null) {
            null
        } else {
            enumValues<FundNetwork>()[value]
        }
    }
}