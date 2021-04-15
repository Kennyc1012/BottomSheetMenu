package com.kennyc.bottomsheet.menu

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BottomSheetMenu(private val context: Context) : Menu {

    private var isQwerty: Boolean = false

    private val items: ArrayList<BottomSheetMenuItem> = ArrayList()

    override fun add(title: CharSequence): MenuItem {
        return add(0, 0, 0, title)
    }

    override fun add(titleRes: Int): MenuItem {
        return add(0, 0, 0, titleRes)
    }

    override fun add(groupId: Int, itemId: Int, order: Int, titleRes: Int): MenuItem {
        return add(groupId, itemId, order, context.resources.getString(titleRes))
    }

    override fun add(groupId: Int, itemId: Int, order: Int, title: CharSequence): MenuItem {
        val item = BottomSheetMenuItem(context, groupId, itemId, 0, order, title)
        // TODO Order is ignored here.
        items.add(item)
        return item
    }

    override fun addIntentOptions(groupId: Int, itemId: Int, order: Int,
                                  caller: ComponentName, specifics: Array<Intent>, intent: Intent, flags: Int,
                                  outSpecificItems: Array<MenuItem>?): Int {
        val pm = context.packageManager
        val lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0)
        val size = lri.size

        if (flags and Menu.FLAG_APPEND_TO_GROUP == 0) {
            removeGroup(groupId)
        }

        for (i in 0 until size) {
            val ri = lri[i]
            val rintent = Intent(
                    if (ri.specificIndex < 0) intent else specifics[ri.specificIndex])
            rintent.component = ComponentName(
                    ri.activityInfo.applicationInfo.packageName,
                    ri.activityInfo.name)
            val item = add(groupId, itemId, order, ri.loadLabel(pm))
                    .setIcon(ri.loadIcon(pm))
                    .setIntent(rintent)
            if (outSpecificItems != null && ri.specificIndex >= 0) {
                outSpecificItems[ri.specificIndex] = item
            }
        }

        return size
    }

    override fun addSubMenu(title: CharSequence): SubMenu? {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun addSubMenu(titleRes: Int): SubMenu? {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun addSubMenu(groupId: Int, itemId: Int, order: Int,
                            title: CharSequence): SubMenu? {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun addSubMenu(groupId: Int, itemId: Int, order: Int, titleRes: Int): SubMenu? {
        throw UnsupportedOperationException("Not Supported")
    }

    override fun clear() {
        items.clear()
    }

    override fun close() {}

    private fun findItemIndex(id: Int): Int {
        val itemCount = items.size
        for (i in 0 until itemCount) {
            if (items[i].itemId == id) {
                return i
            }
        }

        return -1
    }

    override fun findItem(id: Int): MenuItem {
        return items[findItemIndex(id)]
    }

    override fun getItem(index: Int): MenuItem {
        return items[index]
    }

    override fun hasVisibleItems(): Boolean {
        val itemCount = items.size

        for (i in 0 until itemCount) {
            if (items[i].isVisible) {
                return true
            }
        }

        return false
    }

    private fun findItemWithShortcut(keyCode: Int, event: KeyEvent): BottomSheetMenuItem? {
        // TODO Make this smarter.
        val qwerty = isQwerty
        val itemCount = items.size

        for (i in 0 until itemCount) {
            val item = items[i]
            val shortcut = if (qwerty)
                item.alphabeticShortcut
            else
                item.numericShortcut
            if (keyCode == shortcut.toInt()) {
                return item
            }
        }
        return null
    }

    override fun isShortcutKey(keyCode: Int, event: KeyEvent): Boolean {
        return findItemWithShortcut(keyCode, event) != null
    }

    override fun performIdentifierAction(id: Int, flags: Int): Boolean {
        val index = findItemIndex(id)
        return if (index < 0) {
            false
        } else items[index].invoke()

    }

    override fun performShortcut(keyCode: Int, event: KeyEvent, flags: Int): Boolean {
        val item = findItemWithShortcut(keyCode, event) ?: return false

        return item.invoke()
    }

    override fun removeGroup(groupId: Int) {
        var itemCount = items.size
        var i = 0
        while (i < itemCount) {
            if (items[i].groupId == groupId) {
                items.removeAt(i)
                itemCount--
            } else {
                i++
            }
        }
    }

    override fun removeItem(id: Int) {
        items.removeAt(findItemIndex(id))
    }

    override fun setGroupCheckable(group: Int, checkable: Boolean,
                                   exclusive: Boolean) {
        val itemCount = items.size

        for (i in 0 until itemCount) {
            val item = items[i]
            if (item.groupId == group) {
                item.isCheckable = checkable
                item.setExclusiveCheckable(exclusive)
            }
        }
    }

    override fun setGroupEnabled(group: Int, enabled: Boolean) {
        val itemCount = items.size

        for (i in 0 until itemCount) {
            val item = items[i]
            if (item.groupId == group) {
                item.isEnabled = enabled
            }
        }
    }

    override fun setGroupVisible(group: Int, visible: Boolean) {
        val itemCount = items.size

        for (i in 0 until itemCount) {
            val item = items[i]
            if (item.groupId == group) {
                item.isVisible = visible
            }
        }
    }

    override fun setQwertyMode(isQwerty: Boolean) {
        this.isQwerty = isQwerty
    }

    override fun size(): Int {
        return items.size
    }

    class MenuItemBuilder(private val context: Context,
                          id: Int,
                          title: String = "NULL",
                          icon: Drawable? = null) {

        var title: String = title; private set
        var icon: Drawable? = icon; private set
        var id: Int = id; private set

        fun setTitle(@StringRes title: Int): MenuItemBuilder = setTitle(context.getString(title))

        fun setTitle(title: String): MenuItemBuilder {
            this.title = title
            return this
        }

        fun setIcon(icon: Drawable?): MenuItemBuilder {
            this.icon = icon
            return this
        }

        fun setIcon(@DrawableRes icon: Int): MenuItemBuilder = setIcon(ResourcesCompat.getDrawable(context.resources, icon, context.theme))

        fun build(): MenuItem = BottomSheetMenuItem(context, id, title, icon)
    }
}