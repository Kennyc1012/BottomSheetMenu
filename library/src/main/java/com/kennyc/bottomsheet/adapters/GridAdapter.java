package com.kennyc.bottomsheet.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.StyleRes;
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
    private final List<MenuItem> items;

    private final LayoutInflater inflater;

    private boolean isGrid;

    public GridAdapter(Context context, List<MenuItem> items, boolean isGrid) {
        this.items = items;
        this.isGrid = isGrid;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public MenuItem getItem(int position) {
        return items.get(position);
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
            convertView = inflater.inflate(isGrid ? R.layout.bottom_sheet_grid_item : R.layout.bottom_sheet_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drawable menuIcon = item.getIcon();
        holder.icon.setImageDrawable(menuIcon);
        holder.icon.setVisibility(menuIcon != null ? View.VISIBLE : View.GONE);
        holder.title.setText(item.getTitle());
        return convertView;
    }
}
