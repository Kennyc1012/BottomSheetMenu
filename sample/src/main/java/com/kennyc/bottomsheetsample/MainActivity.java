package com.kennyc.bottomsheetsample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomSheetListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.gridBottomSheet).setOnClickListener(this);
        findViewById(R.id.listBottomSheet).setOnClickListener(this);
        findViewById(R.id.darkBottomSheet).setOnClickListener(this);
        findViewById(R.id.darkGridBottomSheet).setOnClickListener(this);
        findViewById(R.id.customBottomSheet).setOnClickListener(this);
        findViewById(R.id.customGridBottomSheet).setOnClickListener(this);
        findViewById(R.id.bottomSheetRuntimeMenu).setOnClickListener(this);
        findViewById(R.id.gridSheetRuntimeMenuItems).setOnClickListener(this);
        findViewById(R.id.bottomSheetTintIcons).setOnClickListener(this);
        findViewById(R.id.gridSheetTintIcons).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listBottomSheet:
                new BottomSheet.Builder(this, R.menu.list_sheet)
                        .setListener(this)
                        .show();
                break;

            case R.id.gridBottomSheet:
                new BottomSheet.Builder(this, R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
                break;

            case R.id.darkBottomSheet:
                new BottomSheet.Builder(this, R.menu.list_sheet)
                        .setListener(this)
                        .dark()
                        .show();
                break;

            case R.id.darkGridBottomSheet:
                new BottomSheet.Builder(this, R.menu.grid_sheet)
                        .grid()
                        .dark()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
                break;

            case R.id.customBottomSheet:
                new BottomSheet.Builder(this, R.menu.list_sheet, R.style.BottomSheet_Custom)
                        .setListener(this)
                        .show();
                break;

            case R.id.customGridBottomSheet:
                new BottomSheet.Builder(this, R.menu.grid_sheet, R.style.BottomSheet_Custom)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .show();
                break;
            case R.id.bottomSheetRuntimeMenu:
                new BottomSheet.Builder(this)
                        .setMenu(getMenuFromRes(R.menu.list_sheet))
                        .setListener(this)
                        .show();
                break;
            case R.id.gridSheetRuntimeMenuItems:
                new BottomSheet.Builder(this)
                        .grid()
                        .setTitle("Randomized runtime menu items")
                        .setMenuItems(getMenuItemsFromRes(R.menu.list_sheet))
                        .setListener(this)
                        .show();
                break;
            case R.id.bottomSheetTintIcons:
                new BottomSheet.Builder(this, R.menu.tint_sheet)
                        .setMenuItemTintColorRes(R.color.palette_teal_500)
                        .setListener(this)
                        .show();
                break;
            case R.id.gridSheetTintIcons:
                new BottomSheet.Builder(this, R.menu.grid_sheet)
                        .grid()
                        .setMenuItemTintColor(generateRandomColor())
                        .setTitle("Random tinted icons")
                        .setListener(this)
                        .show();
                break;
        }
    }

    private int generateRandomColor() {
        final Random random = new Random(System.nanoTime());
        return Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private Menu getMenuFromRes(@MenuRes int menuResId) {
        // inflate our menu
        final PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(menuResId);
        return popupMenu.getMenu();
    }

    private ArrayList<MenuItem> getMenuItemsFromRes(@MenuRes int menuResId) {
        final ArrayList<MenuItem> menuItems = new ArrayList<>();
        final Menu menu = getMenuFromRes(menuResId);

        // get each menu item from our menu and add it to the list
        final int menuSize = menu.size();
        for (int i = 0; i < menuSize; i++) {
            menuItems.add(menu.getItem(i));
        }

        // randomize it
        Collections.shuffle(menuItems);

        // remove first item
        menuItems.remove(0);

        return menuItems;
    }

    @Override
    public void onSheetShown() {
        Log.v(TAG, "onSheetShown");
    }

    @Override
    public void onSheetItemSelected(MenuItem item) {
        Toast.makeText(getApplicationContext(), item.getTitle() + " Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSheetDismissed() {
        Log.v(TAG, "onSheetDismissed");
    }
}
