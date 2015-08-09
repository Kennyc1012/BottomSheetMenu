package com.kennyc.bottomsheetsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

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
        }
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
