package com.kizune.bucks.model

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.SET_NULL
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kizune.bucks.R
import kotlinx.serialization.Serializable

enum class MovementType(
    @StringRes val id: Int,
) {
    In(R.string.amount_type_in),
    Out(R.string.amount_type_out)
}

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Fund::class,
            parentColumns = ["fundID"],
            childColumns = ["fundInID"],
            // Per permettere ai movimenti di esistere anche se uno dei due fondi viene cancellato
            onDelete = SET_NULL
        ),
        ForeignKey(
            entity = Fund::class,
            parentColumns = ["fundID"],
            childColumns = ["fundOutID"],
            // Per permettere ai movimenti di esistere anche se uno dei due fondi viene cancellato
            onDelete = SET_NULL
        ),
    ],
    indices = [
        Index(
            value = ["fundInID"]
        ),
        Index(
            value = ["fundOutID"]
        )
    ]
)

@Serializable
data class Movement(
    @PrimaryKey val movementID: Long = 0,
    val fundInID: Long? = null,
    val fundOutID: Long? = null,
    val title: String = "",
    val description: String = "",
    var amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
)
