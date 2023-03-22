package com.kizune.bucks.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kizune.bucks.model.*
import com.kizune.bucks.utils.getFormattedSerial

@Composable
fun BucksCard(
    fund: Fund,
    modifier: Modifier = Modifier,
) {
    val colors = fund.getColorsForCard()
    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = colors.first)),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
        ) {
            CardBankLogo(
                fund = fund,
                modifier = Modifier
                    .height(90.dp)
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
            )
            CardContent(
                fund = fund,
                contentColor = colors.second
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BucksCard(
    fund: Fund,
    onCardClick: (Fund) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = fund.getColorsForCard()
    Card(
        onClick = { onCardClick(fund) },
        colors = CardDefaults.cardColors(containerColor = colorResource(id = colors.first)),
        shape = MaterialTheme.shapes.large,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
        ) {
            CardBankLogo(
                fund = fund,
                modifier = Modifier
                    .height(90.dp)
                    .align(Alignment.CenterEnd)
                    .padding(horizontal = 16.dp)
            )
            CardContent(
                fund = fund,
                contentColor = colors.second
            )
        }
    }
}

@Composable
fun CardBankLogo(
    fund: Fund,
    modifier: Modifier = Modifier
) {
    val data = fund.getBankLogoResForCard()
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build(),
        placeholder = asyncImagePlaceholder(placeholder = data),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Composable
fun CardNetwork(
    fund: Fund,
    modifier: Modifier = Modifier
) {
    val data = fund.getNetworkIconResForCard()
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(data)
            .crossfade(true)
            .build(),
        placeholder = asyncImagePlaceholder(placeholder = data),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Composable
private fun CardContent(
    fund: Fund,
    contentColor: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = fund.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colorResource(id = contentColor),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            CardNetwork(
                fund = fund,
                modifier = Modifier.height(24.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (fund.title.isNotEmpty()) stringResource(fund.type.id) else "",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = contentColor)
        )
        Text(
            text = if (fund.title.isNotEmpty() && fund.hasSerial()) fund.serialNumber.getFormattedSerial() else "",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = contentColor)
        )
        Spacer(Modifier.weight(1f))
        CashText(
            value = fund.cash,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = colorResource(id = contentColor),
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun BucksFundIcon(
    fund: Fund,
    modifier: Modifier = Modifier,
    containerSize: Dp = 56.dp,
    contentSize: Dp = 40.dp,
    cornerSize: Dp = 8.dp,
) {
    val context = LocalContext.current
    val data = fund.getBankLogoResForCard()
    val colors = fund.getColorsForCard()

    Card(
        colors = CardDefaults.cardColors(containerColor = colorResource(id = colors.first)),
        shape = RoundedCornerShape(cornerSize),
        modifier = modifier.size(containerSize)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(data)
                    .crossfade(true)
                    .build(),
                placeholder = asyncImagePlaceholder(placeholder = data),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(contentSize)
                    .align(Alignment.Center)
            )
        }
    }
}
