package com.kennyc.bottomsheet;

import android.support.annotation.NonNull;
import android.view.MenuItem;

/**
 * Created by kcampagna on 8/9/15.
 */
public interface BottomSheetListener {

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
     * @param bottomSheet The {@link BottomSheet} that was dismissed
     * @param which       Which button was selected if a message was showing. Values include {@link android.app.Dialog#BUTTON_POSITIVE},
     *                    {@link android.app.Dialog#BUTTON_NEGATIVE}, {@link android.app.Dialog#BUTTON_NEUTRAL}, and {@link Integer#MIN_VALUE} which
     *                    represents nothing being pressed
     */
    void onSheetDismissed(@NonNull BottomSheet bottomSheet, int which);
}
