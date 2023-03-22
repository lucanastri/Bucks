package com.kizune.bucks.ui.slide

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.kizune.bucks.ui.settings.SettingsViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalPagerApi::class)
class SlideUIHandler(
    val viewModel: SettingsViewModel,
    val pagerState: PagerState,
    val coroutineScope: CoroutineScope,
    val moveTaskToBack: () -> Unit,
) : SlideUIEvents {

    override fun onDoneClicked() {
        viewModel.setOnBoarding(true)
    }

    override fun onNextClicked() {
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    override fun onBackButton() {
        coroutineScope.launch {
            if (pagerState.currentPage != 0) {
                pagerState.animateScrollToPage(pagerState.currentPage - 1)
            } else {
                moveTaskToBack()
            }
        }
    }
}

/**
 * Mock class for Preview
 */
class SlideUIHandlerMock : SlideUIEvents {
    override fun onDoneClicked() {}
    override fun onNextClicked() {}
    override fun onBackButton() {}
}