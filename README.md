#[BottomSheet](http://www.google.com/design/spec/components/bottom-sheets.html#)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BottomSheet-green.svg?style=flat)](https://android-arsenal.com/details/1/2315)

![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/list.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/grid.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/tablet_list.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/tablet_grid.png)

#Features
- Both list and grid style
- Light and Dark theme as well as custom themeing options
- XML style support
- Tablet support
- API 14+


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
// Sets the BottomSheet to use the grid style
grid()

// Sets the BottomSheet to use the dark theme
dark()

// If the BottomSheet can be dismiss via the back button or pressing outside 
setCancelable(boolean cancelable)

// Sets the menu resource to be used
setSheet(@MenuRes int sheetItems)

// Sets the Menu to be used
setMenu(Menu menu)

// Sets the MenuItems to be used
setMenuItems(List<MenuItem> items)

// Sets the tint color of the MenuItem icons
setMenuItemTintColor(@ColorInt int menuItemTintColor)
setMenuItemTintColorRes(@ColorRes int colorRes)

// Sets the style 
setStyle(@StyleRes int style)
```

#Styling
BottomSheet comes with both a Light and Dark theme to accommodate most scenarios. However, if you want to customize the color more, you can create your own style and supply it to the builder.
```xml
<style name="MyBottomSheetStyle" parent="@style/BottomSheet">
        <item name="bottom_sheet_bg_color">@color/my_color</item>
        <item name="bottom_sheet_title_color">@color/my_color_2</item>
        <item name="bottom_sheet_list_item_color">@color/my_color_3</item>
        <item name="bottom_sheet_grid_item_color">@color/my_color_4</item>
        <!-- Tints the menu item icon to the specified color -->
        <item name="bottom_sheet_item_icon_color">@color/my_color_5</item>
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

#Including in your project
To include BottomSheet in your project, make the following changes to your build.gradle file

## Add repository 
```groovy
repositories {
    maven { url 'https://dl.bintray.com/kennyc1012/maven' }
}
```
## Add dependency
```groovy
dependencies {
    compile 'com.kennyc:bottomsheet:1.1.2'
}
```

#Contribution
Pull requests are welcomed and encouraged. If you experience any bugs, please file an [issue](https://github.com/Kennyc1012/BottomSheet/issues)

License
=======

    Copyright 2015 Kenny Campagna

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
