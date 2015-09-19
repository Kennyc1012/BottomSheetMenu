#[BottomSheet](http://www.google.com/design/spec/components/bottom-sheets.html#)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BottomSheet-green.svg?style=flat)](https://android-arsenal.com/details/1/2315)

![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/list.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/grid.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/tablet_list.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/tablet_grid.png)
![screenshot](https://github.com/Kennyc1012/BottomSheet/blob/master/art/share_list.png)

#Features
- Both list and grid style
- Custom Views
- Simple Messages
- Light and Dark theme as well as custom themeing options
- XML style support
- Tablet support
- Share Intent Picker
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
new BottomSheet.Builder(getActivity())
  .setSheet(R.menu.bottom_sheet)
  .setTitle(R.string.options)
  .setListener(myListener)
  .show();
  ```
#Simple Messages
BottomSheet can also display a simple message like a standard dialog. Setting one up is just as simple
```java
new BottomSheet.Builder(this)
    .setTitle(R.string.title)
    .setMessage(R.string.message)
    .setPositiveButton(R.string.ok)
    .setNegativeButton(R.string.close)
    .setIcon(R.drawable.my_icon)
    .setListener(myListener)
    .show();
```

To handle which button was pressed, the [BottomSheetListener.onSheetDismissed(int)](https://github.com/Kennyc1012/BottomSheet/blob/master/library/src/main/java/com/kennyc/bottomsheet/BottomSheetListener.java#L29) specifies which button was pressed using the constant values BUTTON_POSITIVE, BUTTON_NEGATIVE, and BUTTON_NEUTRAL from the [Dialog](http://developer.android.com/reference/android/app/Dialog.html) class. If neither button was pressed and the dialog was just dismissed, which will be Integer.MIN_VALUE

#Custom Views
For even further customization, you can set the BottomSheet to use a custom view. 
```java
View v = ...

new BottomSheet.Builder(this)
    .setView(v)
    .show();
```
There are a few limitations when using a custom view. First, it <b>MUST</b> have a background set or it will appear transparent. Second, the root layout dimensions should always be ```layout_width="match_parent``` and ```layout_height="wrap_content```. Lastly, you will need to manage any click events as the BottomSheetListener can not determine anything from your custom view. If setting a custom view, all other builder settings will be ignored. 

#Styling
BottomSheet comes with both a Light and Dark theme to accommodate most scenarios. However, if you want to customize the color more, you can create your own style and supply it to the builder.
</br> Customizable attributes are:
```xml
<attr name="bottom_sheet_bg_color" format="color" />
    <attr name="bottom_sheet_title_text_appearance" format="reference" />
    <attr name="bottom_sheet_list_text_appearance" format="reference" />
    <attr name="bottom_sheet_grid_text_appearance" format="reference" />
    <attr name="bottom_sheet_message_text_appearance" format="reference" />
    <attr name="bottom_sheet_message_title_text_appearance" format="reference" />
    <attr name="bottom_sheet_button_text_appearance" format="reference" />
    <attr name="bottom_sheet_item_icon_color" format="color" />
```
    
Then create a style and pass it into the Builder
```xml  
<style name="MyBottomSheetStyle" parent="@style/BottomSheet">
    <item name="bottom_sheet_bg_color">@android:color/holo_blue_light</item>
    <item name="bottom_sheet_title_text_appearance">@style/TitleAppearance</item>
    <item name="bottom_sheet_list_text_appearance">@style/ListAppearance</item>
    <item name="bottom_sheet_grid_text_appearance">@style/GridAppearance</item>
</style>

<style name="TitleAppearance" parent="BottomSheet.Title.TextAppearance">
    <item name="android:textColor">@android:color/holo_green_light</item>
</style>

<style name="ListAppearance" parent="BottomSheet.ListItem.TextAppearance">
    <item name="android:textColor">@android:color/holo_red_light</item>
    <item name="android:textSize">18sp</item>
</style>

<style name="GridAppearance" parent="BottomSheet.GridItem.TextAppearance">
    <item name="android:textColor">@android:color/holo_red_light</item>
    <item name="android:textSize">20sp</item>
</style>
```

```java
new BottomSheet.Builder(getActivity(), R.style.MyBottomSheetStyle)
  .setSheet(R.menu.bottom_sheet)
  .setTitle(R.string.options)
  .setListener(myListener)
  .show();
```

##Icons
Based on the [Material Design Guidelines](http://www.google.com/design/spec/components/bottom-sheets.html#bottom-sheets-specs), icons for a linear list styled BottomSheet should be 24dp, where as a grid styled BottomSheet should be 48dp.

#Share Intents
BottomSheet can also be used to create a Share Intent Picker that will be styled like the ones found in Android 5.x+. To create one, simply call one of the static  ```createShareBottomSheet``` methods.
```java
// Create the intent for sharing
Intent intent = new Intent(Intent.ACTION_SEND);
intent.setType("text/*");
intent.putExtra(Intent.EXTRA_TEXT, "My text to share");
// Pass the intent into the createShareBottomSheet method to generate the BottomSheet.
BottomSheet share = BottomSheet.createShareBottomSheet(getActivity(), intent, "My Title");
// Make sure that it doesn't return null! If the system can not handle the intent, null will be returned.
if (share != null) share.show();
// By default, it will be styled as a list. For a grid, pass the boolean value true after the title parameter
```

#Callbacks
BottomSheet uses the [BottomSheetListener](https://github.com/Kennyc1012/BottomSheet/blob/master/library/src/main/java/com/kennyc/bottomsheet/BottomSheetListener.java) for callbacks
```java
// Called when the BottomSheet it first displayed
onSheetShown()

// Called when the BottomSheet has been dismissed. Passed value represent which button was pressed if displaying
// a simple message
onSheetDismissed(int which)

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
    compile 'com.kennyc:bottomsheet:2.0.0'
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
