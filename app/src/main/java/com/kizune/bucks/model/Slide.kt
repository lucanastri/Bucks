package com.kizune.bucks.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.kizune.bucks.R

data class Slide(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int,
    val attribution: String,
)

val slides = listOf(
    Slide(
        title = R.string.slide_title_1,
        description = R.string.slide_description_1,
        image = R.drawable.image_1,
        attribution = "Designed by pch.vector / Freepik"
    ),
    Slide(
        title = R.string.slide_title_2,
        description = R.string.slide_description_2,
        image = R.drawable.image_2,
        attribution = "Designed by stories / Freepik"
    ),
    Slide(
        title = R.string.slide_title_3,
        description = R.string.slide_description_3,
        image = R.drawable.image_3,
        attribution = "Designed by stories / Freepik"
    ),
    Slide(
        title = R.string.slide_title_4,
        description = R.string.slide_description_4,
        image = R.drawable.image_4,
        attribution = "Designed by pch.vector / Freepik"
    ),
    Slide(
        title = R.string.slide_title_5,
        description = R.string.slide_description_5,
        image = R.drawable.image_5,
        attribution = "Designed by vector4stock / Freepik"
    ),
)

