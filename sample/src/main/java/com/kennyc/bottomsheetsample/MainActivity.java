package com.kennyc.bottomsheetsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
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
        findViewById(R.id.messageBottomSheet).setOnClickListener(this);
        findViewById(R.id.viewBottomSheet).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listBottomSheet:
                new BottomSheet.Builder(this)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.gridBottomSheet:
                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.darkBottomSheet:
                new BottomSheet.Builder(this)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .dark()
                        .object("Some object")
                        .show();
                break;

            case R.id.darkGridBottomSheet:
                new BottomSheet.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .dark()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.customBottomSheet:
                new BottomSheet.Builder(this, R.style.BottomSheet_Custom)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.customGridBottomSheet:
                new BottomSheet.Builder(this, R.style.BottomSheet_Custom)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.messageBottomSheet:
                new BottomSheet.Builder(this)
                        .setTitle("BottomSheet")
                        .setMessage("With bottom sheet you can also display a simple message dialog")
                        .setPositiveButton("Okay")
                        .setNegativeButton("Close")
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;

            case R.id.viewBottomSheet:
                new BottomSheet.Builder(this)
                        .setView(R.layout.custom_view)
                        .setListener(this)
                        .object("Some object")
                        .show();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey, check out the BottomSheet library https://github.com/Kennyc1012/BottomSheet");
                BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");
                if (bottomSheet != null) bottomSheet.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object object) {
        Log.v(TAG, "onSheetShown with Object " + object);
    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item, @Nullable Object object) {
        Toast.makeText(getApplicationContext(), item.getTitle() + " Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object object, @DismissEvent int dismissEvent) {
        Log.v(TAG, "onSheetDismissed " + dismissEvent);

        switch (dismissEvent) {
            case BottomSheetListener.DISMISS_EVENT_BUTTON_POSITIVE:
                Toast.makeText(getApplicationContext(), "Positive Button Clicked", Toast.LENGTH_SHORT).show();
                break;

            case BottomSheetListener.DISMISS_EVENT_BUTTON_NEGATIVE:
                Toast.makeText(getApplicationContext(), "Negative Button Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
