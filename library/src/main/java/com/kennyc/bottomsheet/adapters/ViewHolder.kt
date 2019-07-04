package com.kennyc.bottomsheet.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.kennyc.bottomsheet.R

internal class ViewHolder(val view: View) {
    val title: TextView = view.findViewById(R.id.title)

    val icon: ImageView = view.findViewById(R.id.icon)
}