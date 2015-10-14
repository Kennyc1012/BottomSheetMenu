package com.kennyc.bottomsheet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kennyc.bottomsheet.R;

import java.util.List;

/**
 * Created by kcampagna on 9/9/15.
 */
public class GridAdapter extends BaseAdapter {
    private final List<MenuItem> mItems;

    private final LayoutInflater mInflater;

    private boolean mIsGrid;

    @StyleRes
    private int mListStyle;

    @StyleRes
    private int mGridStyle;

    private int mTintColor;

    public GridAdapter(Context context, List<MenuItem> items, boolean isGrid, @StyleRes int listStyle, @StyleRes int gridStyle, int menuItemTintColor) {
        mItems = items;
        mIsGrid = isGrid;
        mInflater = LayoutInflater.from(context);
        mListStyle = listStyle;
        mGridStyle = gridStyle;
        mTintColor = menuItemTintColor;
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
            int textAppearance = mIsGrid ? mGridStyle : mListStyle;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.title.setTextAppearance(textAppearance);
            } else {
                holder.title.setTextAppearance(convertView.getContext(), textAppearance);
            }
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
        holder.icon.setVisibility(menuIcon != null ? View.VISIBLE : View.GONE);
        holder.title.setText(item.getTitle());
        return convertView;
    }
}
