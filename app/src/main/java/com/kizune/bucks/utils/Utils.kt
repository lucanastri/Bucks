package com.kizune.bucks.utils

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.kizune.bucks.data.Currency
import com.kizune.bucks.data.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun Double.normalizeData(max: Double): Double {
    return if (max != 0.0) this / max else 0.0
}

fun Long.convertToFormattedDate(
    dateFormat: Int
): String {
    val date = Date(this)
    val format = SimpleDateFormat(enumValues<DateFormat>()[dateFormat].date, Locale.getDefault())
    return format.format(date)
}

fun Long.convertToFormattedDateTime(
    dateFormat: Int
): String {
    val date = Date(this)
    val preference = enumValues<DateFormat>()[dateFormat]
    val format = SimpleDateFormat(preference.date + " " + preference.time, Locale.getDefault())
    return format.format(date)
}

fun Long.isInRange(
    timeInMillis: Long,
): Boolean {
    val end = System.currentTimeMillis()
    val start = end.minus(timeInMillis)
    return this in start..end
}

fun Double.getFormattedCash(
    currency: Int
): String {
    val format = DecimalFormat("0.##")
    return format.format(this) + " " + enumValues<Currency>()[currency].symbol
}

fun String.getFormattedSerial(): String {
    return chunked(4).joinToString(separator = " ")
}

fun serialNumberFormatter(input: AnnotatedString): TransformedText {
    val trimmed =
        if (input.text.length >= 16)
            input.text.substring(0..15)
        else
            input.text
    val annotatedString = AnnotatedString.Builder().run {
        for (i in trimmed.indices) {
            append(trimmed[i])
            if (i % 4 == 3 && i != 15)
                append(" ")
        }
        toAnnotatedString()
    }

    val creditCardOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 3) return offset
            if (offset <= 7) return offset + 1
            if (offset <= 11) return offset + 2
            if (offset <= 16) return offset + 3
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 1
            if (offset <= 14) return offset - 2
            if (offset <= 19) return offset - 3
            return 16
        }
    }

    return TransformedText(annotatedString, creditCardOffsetTranslator)
}

fun enterTransition(duration: Int = 650): EnterTransition {
    return fadeIn(animationSpec = tween(duration))
}

fun exitTransition(duration: Int = 650): ExitTransition {
    return fadeOut(animationSpec = tween(duration))
}