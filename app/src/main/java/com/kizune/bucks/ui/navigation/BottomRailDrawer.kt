package com.kizune.bucks.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kizune.bucks.R

val navigationItems = listOf(
    DashboardScreens.Cards,
    DashboardScreens.Report,
    DashboardScreens.Settings
)

@Composable
fun BottomBar(
    selectedItem: Int,
    onNavigationItemClick: (Int) -> Unit,
) {
    NavigationBar(
        containerColor = colorResource(id = R.color.surface2)
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = stringResource(id = item.resourceId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.resourceId),
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                selected = index == selectedItem,
                onClick = { onNavigationItemClick(index) }
            )
        }
    }
}

@Composable
fun NavigationRail(
    selectedItem: Int,
    onNavigationItemClick: (Int) -> Unit
) {

    NavigationRail(
        containerColor = colorResource(id = R.color.surface2),
    ) {
        Spacer(Modifier.height(16.dp))
        navigationItems.forEachIndexed { index, item ->
            NavigationRailItem(
                icon = {
                    Icon(
                        imageVector = item.imageVector,
                        contentDescription = stringResource(id = item.resourceId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.resourceId),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selected = index == selectedItem,
                onClick = { onNavigationItemClick(index) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    selectedItem: Int,
    onNavigationItemClick: (Int) -> Unit,
) {
    PermanentNavigationDrawer(
        modifier = Modifier.width(300.dp),
        drawerContent = {
            PermanentDrawerSheet(
                drawerContainerColor = colorResource(id = R.color.surface1),
                drawerContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                drawerShape = RoundedCornerShape(bottomEnd = 16.dp)
            ) {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(id = R.string.drawer_title),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                Spacer(Modifier.height(32.dp))
                navigationItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        icon = {
                            Icon(
                                imageVector = item.imageVector,
                                contentDescription = stringResource(id = item.resourceId)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(id = item.resourceId),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        selected = index == selectedItem,
                        onClick = { onNavigationItemClick(index) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    ) {}
}

