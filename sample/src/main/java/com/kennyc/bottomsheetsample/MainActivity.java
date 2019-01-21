package com.kennyc.bottomsheetsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;

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
                new BottomSheetMenuDialogFragment.Builder(this)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .object("Some object")
                        .show(getSupportFragmentManager());
                break;

            case R.id.gridBottomSheet:
                new BottomSheetMenuDialogFragment.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show(getSupportFragmentManager());
                break;

            case R.id.darkBottomSheet:
                new BottomSheetMenuDialogFragment.Builder(this)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .dark()
                        .object("Some object")
                        .show(getSupportFragmentManager());
                break;

            case R.id.darkGridBottomSheet:
                new BottomSheetMenuDialogFragment.Builder(this)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .dark()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show(getSupportFragmentManager());
                break;

            case R.id.customBottomSheet:
                new BottomSheetMenuDialogFragment.Builder(this, R.style.Theme_BottomSheetMenuDialog_Custom)
                        .setSheet(R.menu.list_sheet)
                        .setListener(this)
                        .object("Some object")
                        .show(getSupportFragmentManager());
                break;

            case R.id.customGridBottomSheet:
                new BottomSheetMenuDialogFragment.Builder(this, R.style.Theme_BottomSheetMenuDialog_Custom)
                        .setSheet(R.menu.grid_sheet)
                        .grid()
                        .setTitle("Options")
                        .setListener(this)
                        .object("Some object")
                        .show(getSupportFragmentManager());
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
                DialogFragment bottomSheet = BottomSheetMenuDialogFragment.createShareBottomSheet(getApplicationContext(), intent, "Share");
                if (bottomSheet != null) bottomSheet.show(getSupportFragmentManager(), null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSheetShown(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object) {
        Log.v(TAG, "onSheetShown with Object " + object);
    }

    @Override
    public void onSheetItemSelected(@NonNull BottomSheetMenuDialogFragment bottomSheet, MenuItem item, @Nullable Object object) {
        Toast.makeText(getApplicationContext(), item.getTitle() + " Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSheetDismissed(@NonNull BottomSheetMenuDialogFragment bottomSheet, @Nullable Object object, @DismissEvent int dismissEvent) {
        Log.v(TAG, "onSheetDismissed " + dismissEvent);
    }
}
