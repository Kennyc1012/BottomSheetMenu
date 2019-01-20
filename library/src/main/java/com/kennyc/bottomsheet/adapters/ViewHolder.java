package com.kennyc.bottomsheet.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kennyc.bottomsheet.R;

public class ViewHolder {
    public TextView title;

    public ImageView icon;

    public ViewHolder(View view) {
        title = view.findViewById(R.id.title);
        icon = view.findViewById(R.id.icon);
        view.setTag(this);
    }
}
