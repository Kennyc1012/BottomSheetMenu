package com.kennyc.bottomsheet;

import android.app.Dialog;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.MenuItem;

/**
 * Created by kcampagna on 8/9/15.
 */
public interface BottomSheetListener {

    int DISMISS_EVENT_BUTTON_POSITIVE = Dialog.BUTTON_POSITIVE;

    int DISMISS_EVENT_BUTTON_NEGATIVE = Dialog.BUTTON_NEGATIVE;

    int DISMISS_EVENT_BUTTON_NEUTRAL = Dialog.BUTTON_NEUTRAL;

    int DISMISS_EVENT_SWIPE = -4;

    int DISMISS_EVENT_MANUAL = -5;

    @IntDef({DISMISS_EVENT_BUTTON_NEGATIVE, DISMISS_EVENT_BUTTON_NEUTRAL, DISMISS_EVENT_BUTTON_POSITIVE, DISMISS_EVENT_MANUAL, DISMISS_EVENT_SWIPE})
    public @interface DismissEvent {
    }

    /**
     * Called when the {@link BottomSheet} is first displayed
     *
     * @param bottomSheet The {@link BottomSheet} that was shown
     */
    void onSheetShown(@NonNull BottomSheet bottomSheet);

    /**
     * Called when an item is selected from the list/grid of the {@link BottomSheet}
     *
     * @param bottomSheet The {@link BottomSheet} that had an item selected
     * @param item        The item that was selected
     */
    void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item);

    /**
     * Called when the {@link BottomSheet} has been dismissed
     *
     * @param bottomSheet  The {@link BottomSheet} that was dismissed
     * @param dismissEvent How the {@link BottomSheet} was dismissed. Possible values are: <br/>
     *                     <li>{@link #DISMISS_EVENT_SWIPE}</li>
     *                     <li>{@link #DISMISS_EVENT_MANUAL}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_POSITIVE}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_NEUTRAL}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_NEGATIVE}</li>
     */
    void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int dismissEvent);
}
