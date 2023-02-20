package com.kizune.bucks.model

import androidx.room.Embedded
import androidx.room.Relation

data class FundWithMovements(
    @Embedded val fund: Fund,
    @Relation(
        parentColumn = "fundID",
        entityColumn = "fundInID"
    )
    val movementsIn: List<Movement>,

    @Relation(
        parentColumn = "fundID",
        entityColumn = "fundOutID"
    )
    val movementsOut: List<Movement>,
)
