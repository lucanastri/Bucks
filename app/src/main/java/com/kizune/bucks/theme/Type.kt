package com.kizune.bucks.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kizune.bucks.R

val sarabun = FontFamily(
    Font(R.font.sarabun_regular),
    Font(R.font.sarabun_medium, FontWeight.Medium)
)

val typography = Typography(
    titleLarge = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.6.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = sarabun,
        fontSize = 16.sp,
        lineHeight = 20.8.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.2.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = sarabun,
        fontSize = 12.sp,
        lineHeight = 15.6.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.3.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = sarabun,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        lineHeight = 11.7.sp,
        letterSpacing = 0.sp
    )

)