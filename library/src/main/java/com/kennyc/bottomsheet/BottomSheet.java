package com.kennyc.bottomsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.MenuRes;
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
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kcampagna on 8/7/15.
 */
public class BottomSheet extends Dialog implements AdapterView.OnItemClickListener {
    private static final int NO_RESOURCE = -1;

    private static final String TAG = BottomSheet.class.getSimpleName();

    private static final int[] ATTRS = new int[]{
            R.attr.bottom_sheet_bg_color,
            R.attr.bottom_sheet_title_color,
            R.attr.bottom_sheet_list_item_color,
            R.attr.bottom_sheet_grid_item_color
    };

    private Builder mBuilder;

    private GridAdapter mAdapter;

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

        if (mBuilder == null || mBuilder.sheetItems == NO_RESOURCE) {
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
        initMenu(ta);
        if (mListener != null) mListener.onSheetShown();
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
        view.setBackgroundColor(ta.getColor(0, Color.WHITE));
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
        // Bad hack to inflate a menu
        Resources res = getContext().getResources();
        PopupMenu m = new PopupMenu(getContext(), null);
        m.inflate(mBuilder.sheetItems);
        Menu menu = m.getMenu();
        List<MenuItem> items = new ArrayList<>(menu.size());

        for (int i = 0; i < menu.size(); i++) {
            items.add(menu.getItem(i));
        }

        int listColor = ta.getColor(2, res.getColor(R.color.black_85));
        int gridColor = ta.getColor(3, res.getColor(R.color.black_85));
        mGrid.setAdapter(mAdapter = new GridAdapter(getContext(), items, mBuilder.isGrid, listColor, gridColor));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            MenuItem item = mAdapter.getItem(position);
            mListener.onSheetItemSelected(item);
        }

        dismiss();
    }

    public static class Builder {
        @StyleRes
        int style = R.style.BottomSheet;

        String title = null;

        boolean cancelable = true;

        boolean isGrid = false;

        @MenuRes
        int sheetItems;

        Context context;

        BottomSheetListener listener;

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context    App context
         * @param sheetItems The menu resource for constructing the sheet
         */
        public Builder(Context context, @MenuRes int sheetItems) {
            this.context = context;
            this.sheetItems = sheetItems;
        }

        /**
         * Constructor for creating a {@link BottomSheet}
         *
         * @param context    App context
         * @param sheetItems The menu resource for constructing the sheet
         * @param style      The style for the sheet to use
         */
        public Builder(Context context, @MenuRes int sheetItems, @StyleRes int style) {
            this(context, sheetItems);
            this.style = style;
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

    private static class GridAdapter extends BaseAdapter {
        private List<MenuItem> mItems;

        private boolean mIsGrid;

        private LayoutInflater mInflater;

        private int mListTextColor;

        int mGridTextColor;

        public GridAdapter(Context context, List<MenuItem> items, boolean isGrid, int listTextColor, int gridTextColor) {
            mItems = items;
            mIsGrid = isGrid;
            mInflater = LayoutInflater.from(context);
            mListTextColor = listTextColor;
            mGridTextColor = gridTextColor;
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
            TextView view;

            if (convertView == null) {
                view = (TextView) mInflater.inflate(mIsGrid ? R.layout.bottom_sheet_grid_item : R.layout.bottom_sheet_list_item, parent, false);
                view.setTextColor(mIsGrid ? mGridTextColor : mListTextColor);
            } else {
                view = (TextView) convertView;
            }

            view.setCompoundDrawablesWithIntrinsicBounds(mIsGrid ? null : item.getIcon(), mIsGrid ? item.getIcon() : null, null, null);
            view.setText(item.getTitle());
            return view;
        }
    }
}
