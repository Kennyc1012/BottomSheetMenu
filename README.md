# BottomSheetMenu
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-BottomSheet-green.svg?style=flat)](https://android-arsenal.com/details/1/2315)

<img src="https://github.com/Kennyc1012/BottomSheetMenu/blob/master/art/ss3.png" width="480"/>
<img src="https://github.com/Kennyc1012/BottomSheetMenu/blob/master/art/ss2.png" width="480"/>
<img src="https://github.com/Kennyc1012/BottomSheetMenu/blob/master/art/ss1.png" width="480"/>

# Features
- Both list and grid style
- Light, Dark, and DayNight theme as well as custom themeing options
- Material3 Theme support
- XML style support
- Tablet support
- Share Intent Picker
- API 21+
- Kotlin support


# Using BottomSheetMenu
To get started using BottomSheetMenu, first you'll need to create a menu resource file with the defined actions. 
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

Then create a BottomSheetMenuDialogFragment via the Builder class using either the Builder method calls for java
or named arguments for Kotlin 
```java
new BottomSheetMenuDialogFragment.Builder(getActivity())
  .setSheet(R.menu.bottom_sheet)
  .setTitle(R.string.options)
  .setListener(myListener)
  .setObject(myObject)
  .show(getSupportFragmentManager());
  ```
  
  ```kotilin
  BottomSheetMenuDialogFragment.Builder(context = this,
        sheet = R.menu.bottom_sheet,
        listener = myListener,
        title = R.string.options,
        `object` = myObject)
        .show(supportFragmentManager)
  ```
# Styling
BottomSheetMenu comes with both a Light and Dark theme to accommodate most scenarios. However, if you want to customize itr more, you can create your own style and supply it to the builder.
</br> Customizable attributes are:
```xml
<!-- The text appearance of the title -->
<attr name="bottom_sheet_menu_title_text_appearance" format="reference" />

<!-- The number of columns to show when using the grid style -->
<attr name="bottom_sheet_menu_column_count" format="integer" />

<!-- The text appearance of the list items -->
<attr name="bottom_sheet_menu_list_text_appearance" format="reference" />

<!-- The text appearance of the grid items -->
<attr name="bottom_sheet_menu_grid_text_appearance" format="reference" />

<!-- The text appearance of the close title -->
<attr name="bottom_sheet_menu_close_title_text_appearance" format="reference" />

<!-- The icon used for the close button -->
<attr name="bottom_sheet_menu_close_button_icon" format="reference" />
```
    
Then create a style
```xml  
<style name="MyBottomSheetMenuStyle" parent="@style/Theme.BottomSheetMenuDialog">
    <item name="bottom_sheet_menu_title_text_appearance">@style/TitleAppearance</item>
    <item name="bottom_sheet_menu_list_text_appearance">@style/ListAppearance</item>
    <item name="bottom_sheet_menu_grid_text_appearance">@style/GridAppearance</item>
</style>

<style name="TitleAppearance" parent="TextAppearance.Material3.TitleLarge">
    <item name="android:textColor">@android:color/holo_green_light</item>
</style>

<style name="ListAppearance" parent="TextAppearance.Material3.BodyMedium">
    <item name="android:textColor">@android:color/holo_red_light</item>
    <item name="android:textSize">18sp</item>
</style>

<style name="GridAppearance" parent="TextAppearance.Material3.BodyMedium">
    <item name="android:textColor">@android:color/holo_red_light</item>
    <item name="android:textSize">20sp</item>
</style>
```
Also note that each of these pre-defined styles also have a light and DayNight theme. They are named similary with a `.Light` or `DayNight` added to the end of the style name</br>
`@style/Theme.BottomSheetMenuDialog.Light` `@style/BottomSheetMenu.Title.TextAppearance.Light` etc...


Then finally pass the style into the `Builder` object.
```java
new BottomSheetMenuDialogFragment.Builder(getActivity(), R.style.MyBottomSheetStyle)
  .setSheet(R.menu.bottom_sheet)
  .setTitle(R.string.options)
  .setListener(myListener)
  .show();
```

```kotlin
BottomSheetMenuDialogFragment.Builder(context = this,
        sheet = R.menu.bottom_sheet,
        title = R.string.options,
        listener = myListener,
        style = R.style.MyBottomSheetStyle)
        .show(supportFragmentManager)
```

# Share Intents
BottomSheetMenu can also be used to create a Share Intent Picker that will be styled like the ones found in Android 5.x+. To create one, simply call one of the static  ```createShareBottomSheet``` methods.
```kotlin
Intent(Intent.ACTION_SEND).apply {
    type = "text/*"
    putExtra(Intent.EXTRA_TEXT, "My text to share")
    // Make sure to check that the createBottomSheet method does not return null!! 
    // If the device can not handle the intent, null will be returned
    BottomSheetMenuDialogFragment.createShareBottomSheet(context, this, "My Title")?.show(supportFragmentManager, null)
}
```
For further customization of the share intent including which apps will be either be shown or not shown, see the full signature of [createBottomSheet](https://github.com/Kennyc1012/BottomSheetMenu/blob/master/library/src/main/java/com/kennyc/bottomsheet/BottomSheetMenuDialogFragment.kt#L50)


# Callbacks
BottomSheetMenu uses the [BottomSheetListener](https://github.com/Kennyc1012/BottomSheetMenu/blob/master/library/src/main/java/com/kennyc/bottomsheet/BottomSheetListener.kt) for callbacks
```kotlin
 /**
     * Called when the [BottomSheetMenuDialogFragment] is first displayed
     *
     * @param bottomSheet The [BottomSheetMenuDialogFragment] that was shown
     * @param object      Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     */
    fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?)

    /**
     * Called when an item is selected from the list/grid of the [BottomSheetMenuDialogFragment]
     *
     * @param bottomSheet The [BottomSheetMenuDialogFragment] that had an item selected
     * @param item        The item that was selected
     * @param object      Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     */
    fun onSheetItemSelected(bottomSheet: BottomSheetMenuDialogFragment, item: MenuItem, `object`: Any?)

    /**
     * Called when the [BottomSheetMenuDialogFragment] has been dismissed
     *
     * @param bottomSheet  The [BottomSheetMenuDialogFragment] that was dismissed
     * @param object       Optional [Object] to pass to the [BottomSheetMenuDialogFragment]
     * @param dismissEvent How the [BottomSheetMenuDialogFragment] was dismissed. Possible values are: <br></br>
     *  * [.DISMISS_EVENT_SWIPE]
     *  * [.DISMISS_EVENT_MANUAL]
     *  * [.DISMISS_EVENT_ITEM_SELECTED]
     */
    fun onSheetDismissed(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?, @DismissEvent dismissEvent: Int)
```

# Upgrading to 4.X
- Styles now extend Theme.Material3.* themes
- An app's style should inherit from a MaterialComponent theme. Material3 themes are preferred but not required. 
- Removed `bottom_sheet_menu_selector` attribute
- Removed various resources
- Java 11 is now required to compile project
- MinSdk is now 21, also targeting API 31

# Upgrading to 3.X
- `BottomSheet` has been renamed to `BottomSheetMenuDialogFragment`
- Custom views and simple messages are no longer supported. Please use a [BottomSheetDialogFragment](https://developer.android.com/reference/com/google/android/material/bottomsheet/BottomSheetDialogFragment) and customize it from there
- Many of the theme attributes have been removed or renamed. See the Styling section above for current values
- CollaspingView has been removed. 
- Migration to [AndroidX](https://developer.android.com/jetpack/androidx/) and [Google Material Components](https://github.com/material-components/material-components-android)
- MinSdk is now 19, also targeting API 28

# Upgrading From 1.x
When upgrading to 2.x from a 1.x release, some changes will have to be made.
- All of the builder methods for settings colors have been removed. All customzing should be done through themes.
- The style attributes have been change to text appearances rather than colors.
- The Builder constructor no longer takes a menu object. You will need to call ```setSheet(...)```.
- The ```onSheetDismissed``` callback now takes an int as an argument for simple message support. 
- The gradle dependency has changed and needs to be updated. 

# Including in your project
To include BottomSheet in your project, make the following changes to your build.gradle file

## Add repository 
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
## Add dependency
```groovy
dependencies {
     implementation "com.github.Kennyc1012:BottomSheetMenu:4.1
```

# Contribution
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
