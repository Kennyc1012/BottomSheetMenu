package com.kennyc.bottomsheet;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntegerRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kennyc.bottomsheet.menu.BottomSheetMenu;
import com.kennyc.bottomsheet.menu.BottomSheetMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetMenuDialogFragment extends BottomSheetDialogFragment {

    private Builder builder;

    private TextView title;

    private GridView gridView;

    private LinearLayout container;

    private BottomSheetMenuDialogFragment(@NonNull Builder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(builder);
        container = view.findViewById(R.id.bottom_sheet_container);
        title = container.findViewById(R.id.bottom_sheet_title);
        gridView = container.findViewById(R.id.bottom_sheet_grid);
        createUI();
    }

    private void createUI() {
        if (!TextUtils.isEmpty(builder.title)) {
            title.setText(builder.title);
        } else {
            title.setVisibility(View.GONE);
        }

        // TODO Setup menu
    }

    @Override
    public void onDestroyView() {
        title = null;
        gridView = null;
        container = null;
        super.onDestroyView();
    }

    /**
     * Builder factory used for creating {@link BottomSheet}
     */
    public static class Builder {
        int columnCount = -1;

        String title = null;

        boolean cancelable = true;

        boolean isGrid = false;

        List<MenuItem> menuItems = new ArrayList<>();

        Context context;

        Resources resources;

        BottomSheetListener listener;

        @Nullable
        Object object;

        /**
         * Constructor for creating a {@link BottomSheetMenuDialogFragment}
         *
         * @param context App context
         */
        public Builder(Context context) {
            this.context = context;
            this.resources = context.getResources();
        }

        /**
         * Sets the title of the {@link BottomSheetMenuDialogFragment}
         *
         * @param title String for the title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the title of the {@link BottomSheetMenuDialogFragment}
         *
         * @param title String resource for the title
         * @return
         */
        public Builder setTitle(@StringRes int title) {
            return setTitle(resources.getString(title));
        }

        /**
         * Sets the {@link BottomSheetMenuDialogFragment} to use a grid for displaying options
         *
         * @return
         */
        public Builder grid() {
            isGrid = true;
            return this;
        }

        /**
         * Sets whether the {@link BottomSheetMenuDialogFragment} is cancelable with the {@link KeyEvent#KEYCODE_BACK BACK} key.
         *
         * @param cancelable If the dialog can be canceled
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Sets the {@link BottomSheetListener} to receive callbacks
         *
         * @param listener The {@link BottomSheetListener} to receive callbacks for
         * @return
         */
        public Builder setListener(BottomSheetListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Sets the menu resource to use for the {@link BottomSheetMenuDialogFragment}
         *
         * @param sheetItems The {@link BottomSheetListener} to receive callbacks for
         * @return
         */
        public Builder setSheet(@MenuRes int sheetItems) {
            BottomSheetMenu menu = new BottomSheetMenu(context);
            new MenuInflater(context).inflate(sheetItems, menu);
            return setMenu(menu);
        }

        /**
         * Sets the menu to use for the {@link BottomSheetMenuDialogFragment}
         *
         * @param menu
         * @return
         */
        public Builder setMenu(@Nullable Menu menu) {
            if (menu != null) {
                List<MenuItem> items = new ArrayList<>(menu.size());

                for (int i = 0; i < menu.size(); i++) {
                    items.add(menu.getItem(i));
                }

                return setMenuItems(items);
            }

            return this;
        }

        /**
         * Adds the {@link List} of menu items to use for the {@link BottomSheetMenuDialogFragment}
         *
         * @param menuItems
         * @return
         */
        public Builder setMenuItems(@Nullable List<MenuItem> menuItems) {
            this.menuItems.addAll(menuItems);
            return this;
        }

        /**
         * Adds a {@link MenuItem} to the {@link BottomSheetMenuDialogFragment}. For creating a {@link MenuItem}, see {@link BottomSheetMenuItem}
         *
         * @param item
         * @return
         */
        public Builder addMenuItem(MenuItem item) {
            menuItems.add(item);
            return this;
        }

        /**
         * Sets the number of columns that will be shown when set to a grid style
         *
         * @param columnCount Number of columns to show
         * @return
         */
        public Builder setColumnCount(int columnCount) {
            this.columnCount = columnCount;
            return this;
        }

        /**
         * Sets the number of columns that will be shown when set to a grid style
         *
         * @param columnCount Integer resource containing number of columns to show
         * @return
         */
        public Builder setColumnCountResource(@IntegerRes int columnCount) {
            return setColumnCount(resources.getInteger(columnCount));
        }

        /**
         * Sets the {@link Object} to be passed with the {@link BottomSheetMenuDialogFragment}
         *
         * @param object Optional {@link Object}
         * @return
         */
        public Builder object(@Nullable Object object) {
            this.object = object;
            return this;
        }

        /**
         * Creates the {@link BottomSheetMenuDialogFragment} but does not show it.
         *
         * @return
         */
        public BottomSheetMenuDialogFragment create() {
            return new BottomSheetMenuDialogFragment(this);
        }

        /**
         * Creates the {@link BottomSheetMenuDialogFragment} and shows it.
         *
         * @param manager {@link FragmentManager} the {@link BottomSheetMenuDialogFragment} will be added to
         */
        public void show(@NonNull FragmentManager manager) {
            show(manager, null);
        }

        /**
         * Creates the {@link BottomSheetMenuDialogFragment} and shows it.
         *
         * @param manager @link FragmentManager} the {@link BottomSheetMenuDialogFragment} will be added to
         * @param tag     Optional tag for the {@link BottomSheetDialogFragment}
         */
        public void show(@NonNull FragmentManager manager, @Nullable String tag) {
            create().show(manager, tag);
        }
    }
}
