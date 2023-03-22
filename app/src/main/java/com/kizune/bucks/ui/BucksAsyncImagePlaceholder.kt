package com.kizune.bucks.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource

/**
 * La funzione composable che fa da placeholder alle AsyncImage, in modo da permettere
 * alle preview di mostrare delle immagini placeholder
 */
@Composable
fun asyncImagePlaceholder(@DrawableRes placeholder: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = placeholder)
    } else {
        null
    }
