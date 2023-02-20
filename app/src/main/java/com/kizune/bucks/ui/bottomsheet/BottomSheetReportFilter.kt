package com.kizune.bucks.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R
import com.kizune.bucks.data.ReportFilter

@Composable
fun BottomSheetReportFilter(
    selected: Int,
    onItemClick: (ReportFilter) -> Unit,
) {
    Text(
        text = stringResource(id = R.string.settings_report_filter_subtitle),
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn {
        itemsIndexed(enumValues<ReportFilter>().toList()) { index, item ->
            BottomSheetReportFilterItem(
                selected = selected == index,
                reportFilter = item,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun BottomSheetReportFilterItem(
    selected: Boolean,
    reportFilter: ReportFilter,
    onItemClick: (ReportFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onItemClick(reportFilter) }
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)

    ) {
        Text(
            text = stringResource(id = reportFilter.id),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Spacer(Modifier.width(16.dp))
            BottomSheetIcon(imageVector = Icons.Rounded.Done)
        }
    }
}