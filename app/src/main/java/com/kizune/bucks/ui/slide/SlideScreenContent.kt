package com.kizune.bucks.ui.slide

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.*
import com.kizune.bucks.R
import com.kizune.bucks.model.Slide
import com.kizune.bucks.model.slides
import com.kizune.bucks.ui.BucksFilledIconButton
import com.kizune.bucks.ui.BucksTextButton
import com.kizune.bucks.ui.asyncImagePlaceholder

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SlideScreenContent(
    slideUIHandler: SlideUIHandler,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        BucksTextButton(
            text = R.string.slide_skip,
            onClick = { slideUIHandler.onDoneClicked() },
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .padding(8.dp)
        )

        HorizontalPager(
            count = slides.size,
            state = slideUIHandler.pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { index ->
            SlidePage(slide = slides[index])
        }

        SlideBottom(
            pagerState = slideUIHandler.pagerState,
            onDoneClicked = { slideUIHandler.onDoneClicked() },
            onNextClicked = { slideUIHandler.onNextClicked() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun SlidePage(
    slide: Slide
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(slide.image)
                .build(),
            placeholder = asyncImagePlaceholder(placeholder = slide.image),
            contentDescription = null,
            modifier = Modifier
                .align(alignment = CenterHorizontally)
                .padding(top = 80.dp)
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(Modifier.height(8.dp))
        SlideAttribution(
            attribution = slide.attribution,
            modifier = Modifier
                .align(alignment = CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(id = slide.title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = slide.description),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun SlideBottom(
    pagerState: PagerState,
    onDoneClicked: () -> Unit,
    onNextClicked: () -> Unit,
    modifier: Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        HorizontalPagerIndicator(
            pagerState = pagerState,
            pageCount = 5,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = MaterialTheme.colorScheme.primaryContainer,
            indicatorHeight = 8.dp,
            indicatorShape = RoundedCornerShape(50),
            indicatorWidth = 24.dp,
            spacing = 16.dp,
            modifier = Modifier.weight(1f)
        )

        AnimatedContent(targetState = pagerState.currentPage == 4) { state ->
            when (state) {
                true -> {
                    BucksFilledIconButton(
                        onClick = onDoneClicked,
                        imageVector = Icons.Rounded.Done,
                        contentDescription = R.string.slide_next
                    )
                }
                else -> {
                    BucksFilledIconButton(
                        onClick = onNextClicked,
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = R.string.slide_next
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideAttribution(
    attribution: String,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    Surface(
        onClick = {
            uriHandler.openUri("http://www.freepik.com")
        },
        shape = MaterialTheme.shapes.extraSmall,
        color = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) {
        Text(
            text = attribution,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun SlideScreenLogoContent(
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .align(CenterHorizontally)
                .size(200.dp)
        )
    }
}