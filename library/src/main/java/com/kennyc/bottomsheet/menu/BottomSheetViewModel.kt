package com.kennyc.bottomsheet.menu

import android.view.MenuItem
import androidx.lifecycle.ViewModel
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.kennyc.bottomsheet.R

internal class BottomSheetViewModel : ViewModel() {

    var builder: BottomSheetMenuDialogFragment.Builder? = null
    val style get() = builder?.style ?: R.style.Theme_BottomSheetMenuDialog_Light
    val autoExpand get() = builder?.autoExpand ?: false
    val isGrid get() = builder?.isGrid ?: false
    val cancelable get() = builder?.cancelable ?: true
    val columnCount get() = builder?.columnCount ?: -1
    val menuItems: List<MenuItem> get() = builder?.menuItems ?: emptyList()
    val listener get() = builder?.listener
    val `object` get() = builder?.`object`
    val title get() = builder?.title
    val closeTitle get() = builder?.closeTitle
}