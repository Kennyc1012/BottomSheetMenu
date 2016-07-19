package com.kennyc.bottomsheet;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.kennyc.bottomsheet.adapters.AppAdapter;
import com.kennyc.bottomsheet.adapters.GridAdapter;
import com.kennyc.bottomsheet.menu.BottomSheetMenu;
import com.kennyc.bottomsheet.menu.BottomSheetMenuItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kcampagna on 8/7/15.
 */
public class BottomSheet extends Dialog implements AdapterView.OnItemClickListener, CollapsingView.CollapseListener {
    private static final int MIN_LIST_TABLET_ITEMS = 6;

    private static final int GRID_MIN_COLUMNS = 3;

    private static final int GRID_MAX_COLUMN = 4;

    private static final String TAG = BottomSheet.class.getSimpleName();

    private static final int[] ATTRS = new int[]{
            R.attr.bottom_sheet_bg_color, // 0
            R.attr.bottom_sheet_title_text_appearance, // 1
            R.attr.bottom_sheet_list_text_appearance,// 2
            R.attr.bottom_sheet_grid_text_appearance, // 3
            R.attr.bottom_sheet_message_text_appearance, // 4
            R.attr.bottom_sheet_message_title_text_appearance, // 5
            R.attr.bottom_sheet_button_text_appearance, // 6
            R.attr.bottom_sheet_item_icon_color, // 7
            R.attr.bottom_sheet_grid_spacing, // 8
            R.attr.bottom_sheet_grid_top_padding, // 9
            R.attr.bottom_sheet_grid_bottom_padding, // 10
            R.attr.bottom_sheet_selector, // 11
            R.attr.bottom_sheet_column_count // 12
    };

    private Builder builder;

    private BaseAdapter adapter;

    private GridView grid;

    private CollapsingView collapsingView;

    private BottomSheetListener listener;

    private int which = BottomSheetListener.DISMISS_EVENT_MANUAL;

    private final Runnable dismissRunnable = new Runnable() {
        @Override
        public void run() {
            which = BottomSheetListener.DISMISS_EVENT_SWIPE;
            dismiss();
        }
    };

    /**
     * Default constructor. It is recommended to use the {@link com.kennyc.bottomsheet.BottomSheet.Builder} for creating a BottomSheet
     *
     * @param context App context
     * @param builder {@link com.kennyc.bottomsheet.BottomSheet.Builder} with supplied options for the dialog
     */
    private BottomSheet(Context context, Builder builder) {
        super(context, builder.style);
        this.builder = builder;
        listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!canCreateSheet()) {
            throw new IllegalStateException("Unable to create BottomSheet, missing params");
        }

        Window window = getWindow();
        int width = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_width);
        boolean isTablet = width > 0;
        setCancelable(builder.cancelable);

        if (window != null) {
            window.setLayout(width <= 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        } else {
            Log.e(TAG, "Window came back as null, unable to set defaults");
        }

        TypedArray ta = getContext().obtainStyledAttributes(ATTRS);

        if (builder.view != null) {
            initViewLayout(ta);
        } else if (!TextUtils.isEmpty(builder.message)) {
            initMessageLayout(ta);
        } else {
            initLayout(ta, isTablet, builder.columnCount);

            if (builder.menuItems != null) {
                initMenu(ta);
            } else {
                grid.setAdapter(adapter = new AppAdapter(getContext(), builder.apps, builder.isGrid));
            }
        }

        ta.recycle();
        if (listener != null) listener.onSheetShown(this);
    }

    @Override
    public void dismiss() {
        if (listener != null) listener.onSheetDismissed(this, which);
        super.dismiss();
    }

    /**
     * Initializes the layout for a message
     *
     * @param ta The {@link TypedArray} containing the style attributes
     */
    private void initMessageLayout(TypedArray ta) {
        collapsingView = (CollapsingView) LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_message_layout, null);
        collapsingView.setCollapseListener(this);
        collapsingView.enableDrag(builder.cancelable);
        collapsingView.findViewById(R.id.container).setBackgroundColor(ta.getColor(0, Color.WHITE));

        TextView title = (TextView) collapsingView.findViewById(R.id.title);
        boolean hasTitle = !TextUtils.isEmpty(builder.title) || builder.icon != null;

        if (hasTitle) {
            title.setText(builder.title);
            title.setVisibility(View.VISIBLE);
            title.setCompoundDrawablesWithIntrinsicBounds(builder.icon, null, null, null);
            Compat.setTextAppearance(title, ta.getResourceId(5, R.style.BottomSheet_Message_Title_TextAppearance));
        } else {
            title.setVisibility(View.GONE);
        }

        TextView message = (TextView) collapsingView.findViewById(R.id.message);
        message.setText(builder.message);
        Compat.setTextAppearance(message, ta.getResourceId(4, R.style.BottomSheet_Message_TextAppearance));

        if (!TextUtils.isEmpty(builder.positiveBtn)) {
            Button positive = (Button) collapsingView.findViewById(R.id.positive);
            positive.setText(builder.positiveBtn);
            positive.setVisibility(View.VISIBLE);
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    which = BottomSheetListener.DISMISS_EVENT_BUTTON_POSITIVE;
                    dismiss();
                }
            });

            Compat.setTextAppearance(positive, ta.getResourceId(6, R.style.BottomSheet_Button_TextAppearance));
        }

        if (!TextUtils.isEmpty(builder.negativeBtn)) {
            Button negative = (Button) collapsingView.findViewById(R.id.negative);
            negative.setText(builder.negativeBtn);
            negative.setVisibility(View.VISIBLE);
            negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    which = BottomSheetListener.DISMISS_EVENT_BUTTON_NEGATIVE;
                    dismiss();
                }
            });

            Compat.setTextAppearance(negative, ta.getResourceId(6, R.style.BottomSheet_Button_TextAppearance));
        }

        if (!TextUtils.isEmpty(builder.neutralBtn)) {
            Button neutral = (Button) collapsingView.findViewById(R.id.neutral);
            neutral.setText(builder.neutralBtn);
            neutral.setVisibility(View.VISIBLE);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    which = BottomSheetListener.DISMISS_EVENT_BUTTON_NEUTRAL;
                    dismiss();
                }
            });

            Compat.setTextAppearance(neutral, ta.getResourceId(6, R.style.BottomSheet_Button_TextAppearance));
        }

        setContentView(collapsingView);
    }

    /**
     * Initializes the layout for custom view
     *
     * @param ta The {@link TypedArray} containing the style attributes
     */
    private void initViewLayout(TypedArray ta) {
        collapsingView = new CollapsingView(getContext());
        collapsingView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        collapsingView.setCollapseListener(this);
        collapsingView.enableDrag(builder.cancelable);
        builder.view.setBackgroundColor(ta.getColor(0, Color.WHITE));
        collapsingView.addView(builder.view);
        setContentView(collapsingView);
    }

    /**
     * Initializes the layout a standard {@link BottomSheet}
     *
     * @param ta          The {@link TypedArray} containing the style attributes
     * @param isTablet    If the device is a tablet
     * @param columnCount The number of columns to be shown
     */
    private void initLayout(TypedArray ta, boolean isTablet, int columnCount) {
        collapsingView = (CollapsingView) LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);
        collapsingView.setCollapseListener(this);
        collapsingView.enableDrag(builder.cancelable);
        collapsingView.findViewById(R.id.container).setBackgroundColor(ta.getColor(0, Color.WHITE));

        grid = (GridView) collapsingView.findViewById(R.id.grid);
        grid.setOnItemClickListener(this);
        TextView title = (TextView) collapsingView.findViewById(R.id.title);
        boolean hasTitle = !TextUtils.isEmpty(builder.title);

        if (hasTitle) {
            title.setText(builder.title);
            title.setVisibility(View.VISIBLE);
            Compat.setTextAppearance(title, ta.getResourceId(1, R.style.BottomSheet_Title_TextAppearance));
        } else {
            title.setVisibility(View.GONE);
        }

        if (builder.isGrid) {
            int spacing = ta.getDimensionPixelOffset(8, 0);
            int topPadding = ta.getDimensionPixelOffset(9, 0);
            int bottomPadding = ta.getDimensionPixelOffset(10, 0);
            grid.setVerticalSpacing(spacing);
            grid.setPadding(0, topPadding, 0, bottomPadding);
        } else {
            int padding = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_list_padding);
            grid.setPadding(0, hasTitle ? 0 : padding, 0, padding);
        }

        if (columnCount <= 0) {
            columnCount = ta.getInteger(12, -1);
            if (columnCount <= 0) columnCount = getNumColumns(isTablet);
        }

        grid.setNumColumns(columnCount);

        int selector = ta.getResourceId(11, R.drawable.bs_list_selector);
        grid.setSelector(selector);

        setContentView(collapsingView);
    }

    /**
     * Returns the number of columns used for the {@link BottomSheet}. A list style will use 1 column for a phone, where
     * a tablet will use 2 if there are >= 6 items. When styled as a grid, a phone will use 3 columns, where a tablets will be
     * adjusted based on how many items are to be displayed.
     *
     * @param isTablet If the device is a tablet
     * @return
     */
    private int getNumColumns(boolean isTablet) {
        int numItems;

        if (builder.menuItems != null) {
            numItems = builder.menuItems.size();
        } else {
            numItems = builder.apps.size();
        }

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
     * Initializes the List based on the menu resource
     *
     * @param ta The {@link TypedArray} containing the style attributes
     */
    private void initMenu(TypedArray ta) {
        int listTextAppearance;
        int gridTextAppearance;
        int tintColor;

        listTextAppearance = ta.getResourceId(2, R.style.BottomSheet_ListItem_TextAppearance);
        gridTextAppearance = ta.getResourceId(3, R.style.BottomSheet_GridItem_TextAppearance);
        tintColor = ta.getColor(7, Integer.MIN_VALUE);
        adapter = new GridAdapter(getContext(), builder.menuItems, builder.isGrid, listTextAppearance, gridTextAppearance, tintColor);
        grid.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter instanceof GridAdapter) {
            if (listener != null) {
                MenuItem item = ((GridAdapter) adapter).getItem(position);
                listener.onSheetItemSelected(this, item);
            }
        } else if (adapter instanceof AppAdapter) {
            AppAdapter.AppInfo info = ((AppAdapter) adapter).getItem(position);
            Intent intent = new Intent(builder.shareIntent);
            intent.setComponent(new ComponentName(info.packageName, info.name));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }

        dismiss();
    }

    /**
     * Returns if the {@link BottomSheet} can be created based on the {@link com.kennyc.bottomsheet.BottomSheet.Builder}
     *
     * @return
     */
    private boolean canCreateSheet() {
        return builder != null
                && ((builder.menuItems != null && !builder.menuItems.isEmpty())
                || (builder.apps != null && !builder.apps.isEmpty())
                || builder.view != null
                || !TextUtils.isEmpty(builder.message));
    }

    @Override
    public void onCollapse() {
        // Post a runnable for dismissing to avoid "Attempting to destroy the window while drawing!" error
        if (getWindow() != null && getWindow().getDecorView() != null) {
            getWindow().getDecorView().post(dismissRunnable);
        } else {
            which = BottomSheetListener.DISMISS_EVENT_SWIPE;
            dismiss();
        }
    }

    /**
     * Returns the root view of the {@link BottomSheet} dialog. This should only be called <b><i>AFTER</i></b>
     * the dialog has been inflated.
     *
     * @return
     */
    public View getLayout() {
        return collapsingView;
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent.<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title string resource for the share intent
     * @param isGrid     If the share intent BottomSheet should be grid styled
     * @param appsFilter If provided, share will be limited to contained packaged names
     * @param toExclude  If provided, share will exclude the given package names
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, String shareTitle, boolean isGrid, @Nullable Set<String> appsFilter, @Nullable Set<String> toExclude) {
        if (context == null || intent == null) return null;

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        if (apps != null && !apps.isEmpty()) {
            List<AppAdapter.AppInfo> appResources = new ArrayList<>(apps.size());
            boolean shouldCheckPackages = appsFilter != null && !appsFilter.isEmpty();

            for (ResolveInfo resolveInfo : apps) {
                String packageName = resolveInfo.activityInfo.packageName;

                if (shouldCheckPackages && !appsFilter.contains(packageName)) {
                    continue;
                }

                String title = resolveInfo.loadLabel(manager).toString();
                String name = resolveInfo.activityInfo.name;
                Drawable drawable = resolveInfo.loadIcon(manager);
                appResources.add(new AppAdapter.AppInfo(title, packageName, name, drawable));
            }

            if (toExclude != null && !toExclude.isEmpty()) {
                List<AppAdapter.AppInfo> toRemove = new ArrayList<>();

                for (AppAdapter.AppInfo appInfo : appResources) {
                    if (toExclude.contains(appInfo.packageName)) {
                        toRemove.add(appInfo);
                    }
                }

                if (!toRemove.isEmpty()) appResources.removeAll(toRemove);
            }

            Builder b = new Builder(context)
                    .setApps(appResources, intent)
                    .setTitle(shareTitle);

            if (isGrid) b.grid();
            return b.create();
        }

        return null;
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent.<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title for the share intent
     * @param isGrid     If the share intent BottomSheet should be grid styled
     * @param appsFilter If provided share will be limited to contained packaged names
     * @param toExclude  If provided, share will exclude the given package names
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, @StringRes int shareTitle, boolean isGrid, @Nullable Set<String> appsFilter, @Nullable Set<String> toExclude) {
        return createShareBottomSheet(context, intent, context.getString(shareTitle), isGrid, appsFilter, toExclude);
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent.<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title string resource for the share intent
     * @param isGrid     If the share intent BottomSheet should be grid styled
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, @StringRes int shareTitle, boolean isGrid) {
        return createShareBottomSheet(context, intent, context.getString(shareTitle), isGrid, null, null);
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent.<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title for the share intent
     * @param isGrid     If the share intent BottomSheet should be grid styled
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, String shareTitle, boolean isGrid) {
        return createShareBottomSheet(context, intent, shareTitle, isGrid, null, null);
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent. This will be List styled by default.<br>
     * If grid style is desired, use {@link #createShareBottomSheet(Context, Intent, String, boolean)}<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title for the share intent
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, String shareTitle) {
        return createShareBottomSheet(context, intent, shareTitle, false, null, null);
    }

    /**
     * Returns a {@link BottomSheet} to be used as a share intent like Android 5.x+ Share Intent. This will be list styled by default.<br>
     * If grid style is desired, use {@link #createShareBottomSheet(Context, Intent, String, boolean)}<p>
     * An example of an intent to pass is sharing some form of text:<br>
     * Intent intent = new Intent(Intent.ACTION_SEND);<br>
     * intent.setType("text/*");<br>
     * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
     * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
     * if (bottomSheet != null) bottomSheet.show();<br>
     *
     * @param context    App context
     * @param intent     Intent to get apps for
     * @param shareTitle The optional title for the share intent
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, @StringRes int shareTitle) {
        return createShareBottomSheet(context, intent, context.getString(shareTitle), false, null, null);
    }

    /**
     * Builder factory used for creating {@link BottomSheet}
     */
    public static class Builder {
        @StyleRes
        int style = R.style.BottomSheet;

        int columnCount = -1;

        String title = null;

        boolean cancelable = true;

        boolean isGrid = false;

        List<MenuItem> menuItems;

        Context context;

        Resources resources;

        BottomSheetListener listener;

        List<AppAdapter.AppInfo> apps;

        Intent shareIntent;

        @Nullable
        View view;

        @Nullable
        Drawable icon;

        String message;

        String neutralBtn;

        String negativeBtn;

        String positiveBtn;

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context App context
         */
        public Builder(Context context) {
            this(context, R.style.BottomSheet);
        }

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context App context
         * @param style   The style the {@link BottomSheet} will use
         */
        public Builder(Context context, @StyleRes int style) {
            this.context = context;
            this.style = style;
            this.resources = context.getResources();
        }

        /**
         * Sets the title of the {@link BottomSheet}
         *
         * @param title String for the title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the title of the {@link BottomSheet}
         *
         * @param title String resource for the title
         * @return
         */
        public Builder setTitle(@StringRes int title) {
            return setTitle(resources.getString(title));
        }

        /**
         * Sets the {@link BottomSheet} to use a grid for displaying options. When set, the dialog buttons <b><i>will not</i></b> be shown
         *
         * @return
         */
        public Builder grid() {
            isGrid = true;
            return this;
        }

        /**
         * Sets whether the {@link BottomSheet} is cancelable with the {@link KeyEvent#KEYCODE_BACK BACK} key.
         *
         * @param cancelable
         * @return
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        /**
         * Sets the {@link BottomSheetListener} to receive callbacks
         *
         * @param listener
         * @return
         */
        public Builder setListener(BottomSheetListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * Sets the {@link BottomSheet} to use a dark theme
         *
         * @return
         */
        public Builder dark() {
            style = R.style.BottomSheet_Dark;
            return this;
        }

        /**
         * Sets the style of the {@link BottomSheet}
         *
         * @param style
         * @return
         */
        public Builder setStyle(@StyleRes int style) {
            this.style = style;
            return this;
        }

        /**
         * Sets the menu resource to use for the {@link BottomSheet}
         *
         * @param sheetItems
         * @return
         */
        public Builder setSheet(@MenuRes int sheetItems) {
            BottomSheetMenu menu = new BottomSheetMenu(context);
            new MenuInflater(context).inflate(sheetItems, menu);
            return setMenu(menu);
        }

        /**
         * Sets the menu to use for the {@link BottomSheet}
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
         * Sets the {@link List} of menu items to use for the {@link BottomSheet}
         *
         * @param menuItems
         * @return
         */
        public Builder setMenuItems(@Nullable List<MenuItem> menuItems) {
            this.menuItems = menuItems;
            return this;
        }

        /**
         * Adds a {@link MenuItem} to the {@link BottomSheet}. For creating a {@link MenuItem}, see {@link BottomSheetMenuItem}
         *
         * @param item
         * @return
         */
        public Builder addMenuItem(MenuItem item) {
            if (menuItems == null) menuItems = new ArrayList<>();
            menuItems.add(item);
            return this;
        }

        /**
         * Sets the view the {@link BottomSheet} will show. If called, any attempt to add menu items or show a simgple message will be ignored
         *
         * @param view The view to display
         * @return
         */
        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        /**
         * Sets the view the {@link BottomSheet} will show. If called, any attempt to add menu items or show a simgple message will be ignored
         *
         * @param view The view resource to display
         * @return
         */
        public Builder setView(@LayoutRes int view) {
            return setView(LayoutInflater.from(context).inflate(view, null));
        }

        /**
         * Sets the icon to be used for a message {@link BottomSheet}.
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param icon Icon to use
         * @return
         */
        public Builder setIcon(Drawable icon) {
            this.icon = icon;
            return this;
        }

        /**
         * Sets the icon to be used for a message {@link BottomSheet}. Passing a theme is not required and will be ignored if SDK < 21.
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param icon  Icon resource
         * @param theme Optional theme for devices API 21+
         * @return
         */
        public Builder setIcon(@DrawableRes int icon, @Nullable Resources.Theme theme) {
            Drawable dr;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dr = resources.getDrawable(icon, theme);
            } else {
                dr = resources.getDrawable(icon);
            }

            return setIcon(dr);
        }

        /**
         * Sets the message to be used for the {@link BottomSheet}.
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param message Message to use
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Sets the message to be used for the {@link BottomSheet}.
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param message Message resource
         * @return
         */
        public Builder setMessage(@StringRes int message) {
            return setMessage(resources.getString(message));
        }

        /**
         * Sets the text of the positive button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param positiveButton String of the positive button
         * @return
         */
        public Builder setPositiveButton(String positiveButton) {
            this.positiveBtn = positiveButton;
            return this;
        }

        /**
         * Sets the text of the positive button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param positiveButton String resource of the positive button
         * @return
         */
        public Builder setPositiveButton(@StringRes int positiveButton) {
            return setPositiveButton(resources.getString(positiveButton));
        }

        /**
         * Sets the text of the negative button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param negativeButton String of the negative button
         * @return
         */
        public Builder setNegativeButton(String negativeButton) {
            this.negativeBtn = negativeButton;
            return this;
        }

        /**
         * Sets the text of the negative button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param negativeButton String resource of the negative button
         * @return
         */
        public Builder setNegativeButton(@StringRes int negativeButton) {
            return setNegativeButton(resources.getString(negativeButton));
        }

        /**
         * Sets the text of the neutral button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param neutralButton String neutral of the negative button
         * @return
         */
        public Builder setNeutralButton(String neutralButton) {
            this.neutralBtn = neutralButton;
            return this;
        }

        /**
         * Sets the text of the neutral button for a message {@link BottomSheet}
         * This parameter will be ignored if a {@link View} is supplied to {@link #setView(View)}
         *
         * @param neutralButton String resource neutral of the negative button
         * @return
         */
        public Builder setNeutralButton(@StringRes int neutralButton) {
            return setNeutralButton(resources.getString(neutralButton));
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
         * Sets the apps to be used for a share intent. This is not a public facing method.<p>
         * See {@link BottomSheet#createShareBottomSheet(Context, Intent, String, boolean)} for creating a share intent {@link BottomSheet}
         *
         * @param apps   List of apps to use in the share intent
         * @param intent The {@link Intent} used for creating the share intent
         * @return
         */
        private Builder setApps(List<AppAdapter.AppInfo> apps, Intent intent) {
            this.apps = apps;
            shareIntent = intent;
            return this;
        }

        /**
         * Creates the {@link BottomSheet} but does not show it.
         *
         * @return
         */
        public BottomSheet create() {
            return new BottomSheet(context, this);
        }

        /**
         * Creates the {@link BottomSheet} and shows it.
         */
        public void show() {
            create().show();
        }
    }

    private static class Compat {
        public static void setTextAppearance(@NonNull TextView tv, @StyleRes int textAppearance) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(textAppearance);
            } else {
                tv.setTextAppearance(tv.getContext(), textAppearance);
            }
        }
    }
}
