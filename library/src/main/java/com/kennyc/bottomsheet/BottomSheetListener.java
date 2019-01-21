package com.kennyc.bottomsheet;

import android.view.MenuItem;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by kcampagna on 8/9/15.
 */
public interface BottomSheetListener {

    int DISMISS_EVENT_SWIPE = -4;

    int DISMISS_EVENT_MANUAL = -5;

    int DISMISS_EVENT_ITEM_SELECTED = -6;

    @IntDef({DISMISS_EVENT_MANUAL, DISMISS_EVENT_SWIPE})
    @interface DismissEvent {
    }

    /**
     * Called when the {@link BottomSheetMenuDialogFragment} is first displayed
     *
     * @param bottomSheet The {@link BottomSheetMenuDialogFragment} that was shown
     * @param object      Optional {@link Object} to pass to the {@link BottomSheetMenuDialogFragment}
     */
    void onSheetShown(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object);

    /**
     * Called when an item is selected from the list/grid of the {@link BottomSheetMenuDialogFragment}
     *
     * @param bottomSheet The {@link BottomSheetMenuDialogFragment} that had an item selected
     * @param item        The item that was selected
     * @param object      Optional {@link Object} to pass to the {@link BottomSheetMenuDialogFragment}
     */
    void onSheetItemSelected(@NonNull BottomSheetMenuDialogFragment bottomSheet, MenuItem item, @Nullable Object object);

    /**
     * Called when the {@link BottomSheetMenuDialogFragment} has been dismissed
     *
     * @param bottomSheet  The {@link BottomSheetMenuDialogFragment} that was dismissed
     * @param object       Optional {@link Object} to pass to the {@link BottomSheetMenuDialogFragment}
     * @param dismissEvent How the {@link BottomSheetMenuDialogFragment} was dismissed. Possible values are: <br/>
     *                     <li>{@link #DISMISS_EVENT_SWIPE}</li>
     *                     <li>{@link #DISMISS_EVENT_MANUAL}</li>
     *                     <li>{@link #DISMISS_EVENT_ITEM_SELECTED}</li>
     */
    void onSheetDismissed(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object, @DismissEvent int dismissEvent);
}
