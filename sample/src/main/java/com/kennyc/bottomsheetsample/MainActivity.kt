package com.kennyc.bottomsheetsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.kennyc.bottomsheet.menu.BottomSheetMenu
import kotlin.random.Random


class MainActivity : AppCompatActivity(), View.OnClickListener, BottomSheetListener {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.gridBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.listBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.darkBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.darkGridBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.customBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.customGridBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.dynamicBottomSheet).setOnClickListener(this)
        findViewById<Button>(R.id.dayNightBottomSheet).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.listBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                listener = this,
                `object` = "some object",
                sheet = R.menu.list_sheet
            ).show(supportFragmentManager)

            R.id.gridBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                sheet = R.menu.grid_sheet,
                isGrid = true,
                title = "Options",
                listener = this,
                `object` = "some object"
            ).show(supportFragmentManager)

            R.id.darkBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                sheet = R.menu.list_sheet,
                listener = this,
                `object` = "some object"
            ).dark()
                .show(supportFragmentManager)

            R.id.darkGridBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                sheet = R.menu.grid_sheet,
                isGrid = true,
                title = "Options",
                listener = this,
                `object` = "some object"
            ).dark()
                .show(supportFragmentManager)

            R.id.customBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                style = R.style.Theme_BottomSheetMenuDialog_Custom,
                sheet = R.menu.list_sheet,
                listener = this,
                `object` = "some object"
            ).show(supportFragmentManager)

            R.id.customGridBottomSheet -> BottomSheetMenuDialogFragment.Builder(
                context = this,
                style = R.style.Theme_BottomSheetMenuDialog_Custom,
                sheet = R.menu.grid_sheet,
                isGrid = true,
                title = "Options",
                listener = this,
                `object` = "some object"
            ).show(supportFragmentManager)

            R.id.dynamicBottomSheet -> {
                val items = mutableListOf<MenuItem>()

                for (i in 1..20) {
                    val menuItem = BottomSheetMenu.MenuItemBuilder(
                        applicationContext,
                        i,
                        "Item $i",
                    ) .build()
                    items.add(menuItem)
                }

                BottomSheetMenuDialogFragment.Builder(
                    context = this,
                    listener = this,
                    `object` = "some object",
                    menuItems = items,
                    closeTitle = "Close"
                ).show(supportFragmentManager)
            }

            R.id.dayNightBottomSheet -> {
                BottomSheetMenuDialogFragment.Builder(
                    context = this,
                    listener = this,
                    `object` = "some object",
                    title = "DayNight",
                    sheet = R.menu.list_sheet
                ).dayNight()
                    .show(supportFragmentManager)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/*"
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "Hey, check out the BottomSheet library https://github.com/Kennyc1012/BottomSheet"
                )
                BottomSheetMenuDialogFragment.createShareBottomSheet(
                    applicationContext,
                    intent,
                    "Share"
                )?.show(supportFragmentManager, null)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?) {
        Log.v(TAG, "onSheetShown with Object " + `object`!!)
    }

    override fun onSheetItemSelected(
        bottomSheet: BottomSheetMenuDialogFragment,
        item: MenuItem,
        `object`: Any?
    ) {
        Toast.makeText(applicationContext, item.title.toString() + " Clicked", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onSheetDismissed(
        bottomSheet: BottomSheetMenuDialogFragment,
        `object`: Any?,
        dismissEvent: Int
    ) {
        Log.v(TAG, "onSheetDismissed $dismissEvent")
    }
}