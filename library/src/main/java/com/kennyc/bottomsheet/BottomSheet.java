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
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kcampagna on 8/7/15.
 */
public class BottomSheet extends Dialog implements AdapterView.OnItemClickListener, CollapsingView.CollapseListener {
    private static final int NO_RESOURCE = -1;

    private static final String TAG = BottomSheet.class.getSimpleName();

    private static final int[] ATTRS = new int[]{
            R.attr.bottom_sheet_bg_color,
            R.attr.bottom_sheet_title_color,
            R.attr.bottom_sheet_list_item_color,
            R.attr.bottom_sheet_grid_item_color,
            R.attr.bottom_sheet_item_icon_color
    };

    private Builder mBuilder;

    private BaseAdapter mAdapter;

    private GridView mGrid;

    private TextView mTitle;

    private BottomSheetListener mListener;

    /**
     * Default constructor. It is recommended to use the {@link com.kennyc.bottomsheet.BottomSheet.Builder} for creating a BottomSheet
     *
     * @param context  App context
     * @param builder  {@link com.kennyc.bottomsheet.BottomSheet.Builder} with supplied options for the dialog
     * @param style    Style resource for the dialog
     * @param listener The optional {@link BottomSheetListener} for callbacks
     */
    BottomSheet(Context context, Builder builder, @StyleRes int style, BottomSheetListener listener) {
        super(context, style);
        mBuilder = builder;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!canCreateSheet()) {
            throw new IllegalStateException("Unable to create BottomSheet, missing params");
        }

        Window window = getWindow();

        if (window != null) {
            int width = getContext().getResources().getDimensionPixelSize(R.dimen.bottom_sheet_width);
            window.setLayout(width <= 0 || mBuilder.isGrid ? ViewGroup.LayoutParams.MATCH_PARENT : width, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        } else {
            Log.e(TAG, "Window came back as null, unable to set defaults");
        }

        TypedArray ta = getContext().obtainStyledAttributes(ATTRS);
        initLayout(ta);

        if (mBuilder.menuItems != null) {
            initMenu(ta);
            if (mListener != null) mListener.onSheetShown();
        } else {
            mGrid.setAdapter(mAdapter = new AppAdapter(getContext(), mBuilder.apps, mBuilder.isGrid));
        }

        ta.recycle();
    }

    @Override
    public void dismiss() {
        if (mListener != null) mListener.onSheetDismissed();
        super.dismiss();
    }

    private void initLayout(TypedArray ta) {
        Resources res = getContext().getResources();
        setCancelable(mBuilder.cancelable);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);
        ((CollapsingView) view).setCollapseListener(this);
        view.findViewById(R.id.container).setBackgroundColor(ta.getColor(0, Color.WHITE));
        mGrid = (GridView) view.findViewById(R.id.grid);
        mGrid.setOnItemClickListener(this);
        mTitle = (TextView) view.findViewById(R.id.title);
        boolean hasTitle = !TextUtils.isEmpty(mBuilder.title);

        if (hasTitle) {
            mTitle.setText(mBuilder.title);
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setTextColor(ta.getColor(1, res.getColor(R.color.black_55)));
        } else {
            mTitle.setVisibility(View.GONE);
        }

        if (mBuilder.isGrid) {
            int gridPadding = res.getDimensionPixelSize(R.dimen.bottom_sheet_grid_padding);
            int topPadding = res.getDimensionPixelSize(R.dimen.bottom_sheet_dialog_padding);
            mGrid.setNumColumns(res.getInteger(R.integer.bottomsheet_num_columns));
            mGrid.setVerticalSpacing(res.getDimensionPixelSize(R.dimen.bottom_sheet_grid_spacing));
            mGrid.setPadding(0, topPadding, 0, gridPadding);
        } else {
            int padding = res.getDimensionPixelSize(R.dimen.bottom_sheet_list_padding);
            mGrid.setPadding(0, hasTitle ? 0 : padding, 0, padding);
        }

        setContentView(view);
    }

    private void initMenu(TypedArray ta) {
        Resources res = getContext().getResources();
        int listColor = ta.getColor(2, res.getColor(R.color.black_85));
        int gridColor = ta.getColor(3, res.getColor(R.color.black_85));

        if (mBuilder.menuItemTintColor == Integer.MIN_VALUE) {
            int itemIconColor = ta.getColor(4, Integer.MIN_VALUE);
            if (itemIconColor != Integer.MIN_VALUE) {
                mBuilder.menuItemTintColor = itemIconColor;
            }
        }

        mGrid.setAdapter(mAdapter = new GridAdapter(getContext(), mBuilder, listColor, gridColor));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mAdapter instanceof GridAdapter) {
            if (mListener != null) {
                MenuItem item = ((GridAdapter) mAdapter).getItem(position);
                mListener.onSheetItemSelected(item);
            }
        } else if (mAdapter instanceof AppAdapter) {
            AppAdapter.AppInfo info = ((AppAdapter) mAdapter).getItem(position);
            Intent intent = new Intent(mBuilder.shareIntent);
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
        return mBuilder != null && ((mBuilder.menuItems != null && !mBuilder.menuItems.isEmpty()) || (mBuilder.apps != null && !mBuilder.apps.isEmpty()));
    }

    @Override
    public void onCollapse() {
        dismiss();
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
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, String shareTitle, boolean isGrid) {
        if (context == null || intent == null) return null;

        PackageManager manager = context.getPackageManager();
        List<ResolveInfo> apps = manager.queryIntentActivities(intent, 0);

        if (apps != null && !apps.isEmpty()) {
            List<AppAdapter.AppInfo> appResources = new ArrayList<>(apps.size());

            for (ResolveInfo resolveInfo : apps) {
                String title = resolveInfo.loadLabel(manager).toString();
                String packageName = resolveInfo.activityInfo.packageName;
                String name = resolveInfo.activityInfo.name;
                Drawable drawable = resolveInfo.loadIcon(manager);
                appResources.add(new AppAdapter.AppInfo(title, packageName, name, drawable));
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
     * @param shareTitle The optional title string resource for the share intent
     * @param isGrid     If the share intent BottomSheet should be grid styled
     * @return A {@link BottomSheet} with the apps that can handle the share intent. NULL maybe returned if no
     * apps can handle the share intent
     */
    @Nullable
    public static BottomSheet createShareBottomSheet(Context context, Intent intent, @StringRes int shareTitle, boolean isGrid) {
        return createShareBottomSheet(context, intent, context.getString(shareTitle), isGrid);
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
        return createShareBottomSheet(context, intent, shareTitle, false);
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
        return createShareBottomSheet(context, intent, context.getString(shareTitle), false);
    }

    /**
     * Builder factory used for creating {@link BottomSheet}
     */
    public static class Builder {
        @StyleRes
        int style = NO_RESOURCE;

        String title = null;

        boolean cancelable = true;

        boolean isGrid = false;

        List<MenuItem> menuItems;

        int menuItemTintColor = Integer.MIN_VALUE;

        Context context;

        BottomSheetListener listener;

        List<AppAdapter.AppInfo> apps;

        Intent shareIntent;

        /**
         * Constructor for creating a {@link BottomSheet}, {@link #setSheet(int)} will need to be called to set the menu resource
         *
         * @param context App context
         */
        public Builder(Context context) {
            this(context, NO_RESOURCE, R.style.BottomSheet);
        }

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context    App context
         * @param sheetItems The menu resource for constructing the sheet
         */
        public Builder(Context context, @MenuRes int sheetItems) {
            this(context, sheetItems, R.style.BottomSheet);

        }

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context    App context
         * @param sheetItems The menu resource for constructing the sheet
         * @param style      The style for the sheet to use
         */
        public Builder(Context context, @MenuRes int sheetItems, @StyleRes int style) {
            this.context = context;
            this.style = style;
            if (sheetItems != NO_RESOURCE) setSheet(sheetItems);
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
            return setTitle(context.getString(title));
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
            PopupMenu menu = new PopupMenu(context, null);
            menu.inflate(sheetItems);
            return setMenu(menu.getMenu());
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
         * Resolves the color resource id and tints the menu item icons with the resolved color
         *
         * @param colorRes
         * @return
         */
        public Builder setMenuItemTintColorRes(@ColorRes int colorRes) {
            final int menuItemTintColor = context.getResources().getColor(colorRes);
            return setMenuItemTintColor(menuItemTintColor);
        }

        /**
         * Sets the color to use for tinting the menu item icons
         *
         * @param menuItemTintColor
         * @return
         */
        public Builder setMenuItemTintColor(@ColorInt int menuItemTintColor) {
            this.menuItemTintColor = menuItemTintColor;
            return this;
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
            return new BottomSheet(context, this, style, listener);
        }

        /**
         * Creates the {@link BottomSheet} and shows it.
         */
        public void show() {
            create().show();
        }
    }

    /**
     * Adapter used when creating a {@link BottomSheet} through the {@link com.kennyc.bottomsheet.BottomSheet.Builder} interface
     */
    private static class GridAdapter extends BaseAdapter {
        private final List<MenuItem> mItems;

        private final LayoutInflater mInflater;

        private boolean mIsGrid;

        private int mListTextColor;

        private int mGridTextColor;

        private int mTintColor;

        public GridAdapter(Context context, Builder builder, int listTextColor, int gridTextColor) {
            mItems = builder.menuItems;
            mIsGrid = builder.isGrid;
            mInflater = LayoutInflater.from(context);
            mListTextColor = listTextColor;
            mGridTextColor = gridTextColor;
            mTintColor = builder.menuItemTintColor;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MenuItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getItemId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = getItem(position);
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(mIsGrid ? R.layout.bottom_sheet_grid_item : R.layout.bottom_sheet_list_item, parent, false);
                holder = new ViewHolder(convertView);
                holder.title.setTextColor(mIsGrid ? mGridTextColor : mListTextColor);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Drawable menuIcon = item.getIcon();
            if (mTintColor != Integer.MIN_VALUE && menuIcon != null) {
                // mutate it, so we do not tint the original menu icon
                menuIcon = menuIcon.mutate();
                menuIcon.setColorFilter(new LightingColorFilter(Color.BLACK, mTintColor));
            }

            holder.icon.setImageDrawable(menuIcon);
            holder.title.setText(item.getTitle());
            return convertView;
        }
    }

    /**
     * Adapter used when {@link BottomSheet#createShareBottomSheet(Context, Intent, String, boolean)} is invoked
     */
    private static class AppAdapter extends BaseAdapter {
        List<AppInfo> mApps;

        private LayoutInflater mInflater;

        private int mTextColor;

        private boolean mIsGrid;

        public AppAdapter(Context context, List<AppInfo> info, boolean isGrid) {
            mApps = info;
            mInflater = LayoutInflater.from(context);
            mTextColor = context.getResources().getColor(R.color.black_85);
            mIsGrid = isGrid;
        }

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppInfo appInfo = getItem(position);
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(mIsGrid ? R.layout.bottom_sheet_grid_item : R.layout.bottom_sheet_list_item, parent, false);
                holder = new ViewHolder(convertView);
                holder.title.setTextColor(mTextColor);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.icon.setImageDrawable(appInfo.drawable);
            holder.title.setText(appInfo.title);
            return convertView;
        }

        private static class AppInfo {
            public String title;

            public String packageName;

            public String name;

            public Drawable drawable;

            public AppInfo(String title, String packageName, String name, Drawable drawable) {
                this.title = title;
                this.packageName = packageName;
                this.name = name;
                this.drawable = drawable;
            }
        }
    }

    /**
     * ViewHolder class for the adapters
     */
    private static class ViewHolder {
        public TextView title;

        public ImageView icon;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title);
            icon = (ImageView) view.findViewById(R.id.icon);
            view.setTag(this);
        }
    }
}
