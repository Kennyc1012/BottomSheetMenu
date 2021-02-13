package com.kennyc.bottomsheet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.RestrictTo
import com.kennyc.bottomsheet.R

@RestrictTo(RestrictTo.Scope.LIBRARY)
class GridAdapter(context: Context,
                  private val items: List<MenuItem>,
                  private val isGrid: Boolean) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        val menuIcon = item.icon

        return when (convertView) {
            null -> {
                val itemView = if (isGrid) R.layout.bottom_sheet_grid_item else R.layout.bottom_sheet_list_item
                ViewHolder(inflater.inflate(itemView, parent, false)).apply {
                    view.tag = this
                }
            }
            else -> {
                convertView.tag as ViewHolder
            }
        }.apply {
            icon.setImageDrawable(menuIcon)
            icon.visibility = if (menuIcon != null) View.VISIBLE else View.GONE
            title.text = item.title
            title.isEnabled = item.isEnabled
        }.view
    }

    override fun getItem(position: Int): MenuItem = items[position]

    override fun getItemId(position: Int): Long = getItem(position).itemId.toLong()

    override fun getCount(): Int = items.size

    override fun areAllItemsEnabled(): Boolean = false

    override fun isEnabled(position: Int): Boolean = getItem(position).isEnabled
}