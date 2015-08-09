# BottomSheet

#Designed after the docs at [Google Material Design](http://www.google.com/design/spec/components/bottom-sheets.html#)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/list.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/grid.png)

#Features
- Both list and grid style
- Light and Dark theme as well as custom themeing options
- XML styls support
- Support for API 14+


#Using BottomSheet
To get started using BottomSheet, first you'll need to create a menu resource file with the defined actions. 
```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/share"
        android:icon="@drawable/ic_share_grey_600_24dp"
        android:title="Share" />

    <item
        android:id="@+id/upload"
        android:icon="@drawable/ic_cloud_upload_grey_600_24dp"
        android:title="Upload" />

    <item
        android:id="@+id/copy"
        android:icon="@drawable/ic_content_copy_grey_600_24dp"
        android:title="Copy" />

    <item
        android:id="@+id/print"
        android:icon="@drawable/ic_print_grey_600_24dp"
        android:title="Print" />

</menu>
```

Then create a BottomSheet via the Builder interface
```java
new BottomSheet.Builder(getActivity(), R.menu.bottom_sheet)
  .setTitle(R.string.options)
  .setListener(myListener)
  .show();
  ```
  
##Aditional Builder Fields
```java
// Whether the BottomSheet will use the grid style
grid()

// Whether the BottomSheet will use a dark theme
dark()

// Whether the BottomSheet can be dismiss via the back button or pressing outside 
setCancelable(boolean)
```

#Styling
BottomSheet comes with both a Light and Dark theme to accomidate most scenarios. However, if you want to customize the color more, you can create your own style and supply it to the builder.
```xml
<style name="MyBottomSheetStyle" parent="@style/BottomSheet">
        <item name="bottom_sheet_bg_color">@color/my_color</item>
        <item name="bottom_sheet_title_color">@color/my_color_2</item>
        <item name="bottom_sheet_list_item_color">@color/my_color_3</item>
        <item name="bottom_sheet_grid_item_color">@color/my_color_4</item>
</style>
```

```java
new BottomSheet.Builder(getActivity(), R.menu.bottom_sheet, R.style.MyBottomSheetStyle)
  .setTitle(R.string.options)
  .setListener(myListener)
  .show();
```

##Icon
Based on the [Material Design Guidelines](http://www.google.com/design/spec/components/bottom-sheets.html#bottom-sheets-specs), icons for a linear list styled BottomSheet should be 24dp, where as a grid styled BottomSheet should be 48dp.

#Callbacks
BottomSheet uses the [BottomSheetListener](https://github.com/Kennyc1012/BottomSheet/blob/master/library/src/main/java/com/kennyc/bottomsheet/BottomSheetListener.java) for callbacks
```java
// Called when the BottomSheet it first displayed
onSheetShown()

// Called when the BottomSheet has been dismissed
onSheetDismissed()

// Called when an item is selected from the BottomSheet
onSheetItemSelected(MenuItem item)
```

