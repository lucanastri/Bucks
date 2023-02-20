package com.kizune.bucks

import com.kizune.bucks.utils.convertToFormattedDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatUnitTest {
    @Test
    fun convertLongToDate_isCorrect() {
        var millis = 1673881180000L
        var result = millis.convertToFormattedDate(0)

        assertEquals("16 gen 2023", result)

        millis = 1673881180000L
        result = millis.convertToFormattedDate(1)

        assertEquals("16/01/23", result)

        millis = 1673881180000L
        result = millis.convertToFormattedDate(2)

        assertEquals("16/01/2023", result)

        millis = 1673881180000L
        result = millis.convertToFormattedDate(3)

        assertEquals("2023/01/16", result)

        millis = 1673881180000L
        result = millis.convertToFormattedDate(4)

        assertEquals("2023 gen 16", result)
    }
}