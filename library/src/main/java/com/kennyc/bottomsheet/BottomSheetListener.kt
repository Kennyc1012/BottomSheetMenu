package com.kennyc.bottomsheet

import android.view.MenuItem
import androidx.annotation.IntDef

interface BottomSheetListener {

    @IntDef(DISMISS_EVENT_MANUAL, DISMISS_EVENT_SWIPE)
    annotation class DismissEvent

    /**
     * Called when the [BottomSheetMenuDialogFragment] is first displayed
     *
     * @param bottomSheet The [BottomSheetMenuDialogFragment] that was shown
     * @param object      Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     */
    fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?)

    /**
     * Called when an item is selected from the list/grid of the [BottomSheetMenuDialogFragment]
     *
     * @param bottomSheet The [BottomSheetMenuDialogFragment] that had an item selected
     * @param item        The item that was selected
     * @param object      Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     */
    fun onSheetItemSelected(bottomSheet: BottomSheetMenuDialogFragment, item: MenuItem, `object`: Any?)

    /**
     * Called when the [BottomSheetMenuDialogFragment] has been dismissed
     *
     * @param bottomSheet  The [BottomSheetMenuDialogFragment] that was dismissed
     * @param object       Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     * @param dismissEvent How the [BottomSheetMenuDialogFragment] was dismissed. Possible values are: <br></br>
     *  * [.DISMISS_EVENT_SWIPE]
     *  * [.DISMISS_EVENT_MANUAL]
     *  * [.DISMISS_EVENT_ITEM_SELECTED]
     */
    fun onSheetDismissed(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?, @DismissEvent dismissEvent: Int)

    companion object {
        const val DISMISS_EVENT_SWIPE = -4

        const val DISMISS_EVENT_MANUAL = -5

        const val DISMISS_EVENT_ITEM_SELECTED = -6
    }
}
