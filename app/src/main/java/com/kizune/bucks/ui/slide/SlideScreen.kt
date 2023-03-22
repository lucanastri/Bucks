package com.kizune.bucks.ui.slide

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kizune.bucks.theme.BucksTheme

@Composable
fun SlideScreen(
    widthSize: WindowWidthSizeClass,
    slideUIHandler: SlideUIEvents,
    modifier: Modifier = Modifier,
) {
    BackHandler {
        slideUIHandler.onBackButton()
    }

    Row(modifier = modifier.fillMaxSize()) {
        if (widthSize == WindowWidthSizeClass.Expanded) {
            SlideScreenLogoContent(
                modifier = Modifier.weight(0.5f)
            )
            Spacer(Modifier.width(16.dp))
        }
        SlideScreenContent(
            slideUIHandler = slideUIHandler as SlideUIHandler,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SlidePageCompactPreview() {
    BucksTheme {
        Surface {
            SlideScreen(
                widthSize = WindowWidthSizeClass.Compact,
                slideUIHandler = SlideUIHandlerMock()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 700)
@Composable
fun SlidePageMediumPreview() {
    BucksTheme {
        Surface {
            SlideScreen(
                widthSize = WindowWidthSizeClass.Medium,
                slideUIHandler = SlideUIHandlerMock()
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 1200, heightDp = 800)
@Composable
fun SlidePageExpandedPreview() {
    BucksTheme {
        Surface {
            SlideScreen(
                widthSize = WindowWidthSizeClass.Expanded,
                slideUIHandler = SlideUIHandlerMock()
            )
        }
    }
}

