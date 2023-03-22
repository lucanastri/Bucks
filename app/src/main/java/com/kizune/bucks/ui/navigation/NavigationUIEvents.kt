package com.kizune.bucks.ui.navigation

import com.kizune.bucks.model.Fund

interface NavigationUIEvents {
    fun onInsertMovementClicked(fund: Fund)
    fun onCardClicked(fund: Fund)
    fun onEditClicked(fund: Fund)
    fun onPlaceholderClicked()
    fun onNavigateUp()
    fun onNavigationItemClick(index: Int)
    fun onMoveTaskToBack()
}