package com.kennyc.bottomsheet.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.kennyc.bottomsheet.R;

import java.util.List;


public class AppAdapter extends BaseAdapter {
    List<AppInfo> mApps;

    private LayoutInflater mInflater;

    private int mTextColor;

    private int mLayoutResource;

    public AppAdapter(Context context, List<AppInfo> apps, boolean isGrid) {
        mApps = apps;
        mInflater = LayoutInflater.from(context);
        mTextColor = ContextCompat.getColor(context, R.color.black_85);
        mLayoutResource = isGrid ? R.layout.bottom_sheet_grid_item : R.layout.bottom_sheet_list_item;
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
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder = new ViewHolder(convertView);
            holder.title.setTextColor(mTextColor);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.icon.setImageDrawable(appInfo.drawable);
        holder.title.setText(appInfo.title);
        return convertView;
    }

    public static class AppInfo {
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
