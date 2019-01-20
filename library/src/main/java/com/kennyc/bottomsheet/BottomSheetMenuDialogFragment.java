package com.kennyc.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntegerRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kennyc.bottomsheet.adapters.GridAdapter;
import com.kennyc.bottomsheet.menu.BottomSheetMenu;
import com.kennyc.bottomsheet.menu.BottomSheetMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BottomSheetMenuDialogFragment extends BottomSheetDialogFragment implements GridView.OnItemClickListener {
    private static final int MIN_LIST_TABLET_ITEMS = 6;

    private static final int GRID_MIN_COLUMNS = 3;

    private static final int GRID_MAX_COLUMN = 4;

    private Builder builder;

    private TextView title;

    private GridView gridView;

    private LinearLayout container;

    private BottomSheetListener listener;

    private GridAdapter adapter;

    private int dismissEvent = BottomSheetListener.DISMISS_EVENT_MANUAL;

    private BottomSheetMenuDialogFragment(@NonNull Builder builder) {
        this.builder = builder;
        this.listener = builder.listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_menu, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireActivity(), builder.style);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (container == null || container.getParent() == null) return;
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) container.getParent()).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();

                // Should always be the case
                if (behavior instanceof BottomSheetBehavior) {
                    ((BottomSheetBehavior) behavior).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                        @Override
                        public void onStateChanged(@NonNull View bottomSheet, int state) {
                            if (state == BottomSheetBehavior.STATE_HIDDEN) {
                                dismissEvent = BottomSheetListener.DISMISS_EVENT_SWIPE;
                                dismiss();
                            }
                        }

                        @Override
                        public void onSlide(@NonNull View bottomSheet, float slideOffSet) {
                            // NOOP
                        }
                    });
                }
            }
        });

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(builder);
        container = view.findViewById(R.id.bottom_sheet_container);
        title = container.findViewById(R.id.bottom_sheet_title);
        gridView = container.findViewById(R.id.bottom_sheet_grid);
        createUI();

        if (listener != null) listener.onSheetShown(this, builder.object);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dismissEvent = BottomSheetListener.DISMISS_EVENT_ITEM_SELECTED;
        MenuItem item = adapter.getItem(position);
        if (listener != null) listener.onSheetItemSelected(this, item, builder.object);
        dismiss();
    }

    private void createUI() {
        boolean hasTitle = !TextUtils.isEmpty(builder.title);

        if (hasTitle) {
            title.setText(builder.title);
        } else {
            title.setVisibility(View.GONE);
        }

        if (!builder.isGrid) {
            int padding = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_list_padding);
            gridView.setPadding(0, hasTitle ? 0 : padding, 0, padding);
        }

        gridView.setNumColumns(getNumberColumns());
        gridView.setAdapter(adapter = new GridAdapter(new ContextThemeWrapper(requireActivity(), builder.style), builder.menuItems, builder.isGrid));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onDestroyView() {
        title = null;
        gridView = null;
        container = null;
        super.onDestroyView();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            listener.onSheetDismissed(BottomSheetMenuDialogFragment.this, builder.object, dismissEvent);
        }

        super.onDismiss(dialog);
    }

    private int getNumberColumns() {
        if (builder.columnCount > 0) return builder.columnCount;
        boolean isTablet = getResources().getBoolean(R.bool.bottom_sheet_menu_it_tablet);

        int numItems = builder.menuItems.size();

        if (builder.isGrid) {
            // Show 4 columns if a tablet and the number of its is 4 or >=7
            if ((numItems >= 7 || numItems == GRID_MAX_COLUMN) && isTablet) {
                return GRID_MAX_COLUMN;
            } else {
                return GRID_MIN_COLUMNS;
            }
        }

        // If a tablet with more than 6 items are present, split them into 2 columns
        if (isTablet) return numItems >= MIN_LIST_TABLET_ITEMS ? 2 : 1;

        // Regular phone, one column
        return 1;
    }

    /**
     * Builder factory used for creating {@link BottomSheetMenuDialogFragment}
     */
    public static class Builder {
        @StyleRes
        int style;

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
            this(context, R.style.Theme_BottomSheetMenuDialog_Light);
        }

        /**
         * Constructor for creating a {@link BottomSheetMenuDialogFragment}
         *
         * @param context App context
         * @param style   The style the {@link BottomSheetMenuDialogFragment} will use
         */
        public Builder(Context context, @StyleRes int style) {
            this.context = context;
            this.style = style;
            this.resources = context.getResources();
        }

        /**
         * Sets the {@link BottomSheetMenuDialogFragment} to use a dark theme
         *
         * @return
         */
        public Builder dark() {
            style = R.style.Theme_BottomSheetMenuDialog;
            return this;
        }

        /**
         * Sets the style of the {@link BottomSheetMenuDialogFragment}
         *
         * @param style
         * @return
         */
        public Builder setStyle(@StyleRes int style) {
            this.style = style;
            return this;
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
