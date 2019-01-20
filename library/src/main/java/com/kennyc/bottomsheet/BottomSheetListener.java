package com.kennyc.bottomsheet;

import android.app.Dialog;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
     * @param object      Optional {@link Object} to pass to the {@link BottomSheet}
     */
    void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object object);

    /**
     * Called when an item is selected from the list/grid of the {@link BottomSheet}
     *
     * @param bottomSheet The {@link BottomSheet} that had an item selected
     * @param item        The item that was selected
     * @param object      Optional {@link Object} to pass to the {@link BottomSheet}
     */
    void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item, @Nullable Object object);

    /**
     * Called when the {@link BottomSheet} has been dismissed
     *
     * @param bottomSheet  The {@link BottomSheet} that was dismissed
     * @param object       Optional {@link Object} to pass to the {@link BottomSheet}
     * @param dismissEvent How the {@link BottomSheet} was dismissed. Possible values are: <br/>
     *                     <li>{@link #DISMISS_EVENT_SWIPE}</li>
     *                     <li>{@link #DISMISS_EVENT_MANUAL}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_POSITIVE}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_NEUTRAL}</li>
     *                     <li>{@link #DISMISS_EVENT_BUTTON_NEGATIVE}</li>
     */
    void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object object, @DismissEvent int dismissEvent);
}
