package com.kizune.bucks.utils

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*

@Composable
fun LazyListState.isScrollingUp(): Boolean {

    var prevIndex by remember("list") { mutableStateOf(firstVisibleItemIndex) }
    var prevOffset by remember("list") { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember("list") {

        derivedStateOf {
            if (prevIndex != firstVisibleItemIndex) {
                prevIndex > firstVisibleItemIndex
            } else {
                prevOffset >= firstVisibleItemScrollOffset
            }.also {
                prevOffset = firstVisibleItemScrollOffset
                prevIndex = firstVisibleItemIndex
            }
        }
    }.value
}