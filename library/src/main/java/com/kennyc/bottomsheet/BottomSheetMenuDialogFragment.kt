package com.kennyc.bottomsheet

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import androidx.annotation.IntegerRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kennyc.bottomsheet.adapters.AppAdapter
import com.kennyc.bottomsheet.adapters.GridAdapter
import com.kennyc.bottomsheet.menu.BottomSheetMenu
import com.kennyc.bottomsheet.menu.BottomSheetMenuItem
import com.kennyc.bottomsheet.model.AppInfo
import java.util.*

class BottomSheetMenuDialogFragment() : BottomSheetDialogFragment(),
    AdapterView.OnItemClickListener {

    companion object {
        /**
         * Returns a {@link BottomSheetMenuDialogFragment} to be used as a share intent like Android 5.x+ Share Intent.<p>
         * An example of an intent to pass is sharing some form of text:<br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br>
         * intent.setType("text/`*`");<br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br>
         * if (bottomSheet != null) bottomSheet.show();<br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title string resource for the share intent
         * @param isGrid     If the share intent BottomSheet should be grid styled
         * @param appsFilter If provided, share will be limited to contained packaged names
         * @param toExclude  If provided, share will exclude the given package names
         * @return A {@link BottomSheetMenuDialogFragment} with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            shareTitle: String,
            isGrid: Boolean,
            appsFilter: Set<String>?,
            toExclude: Set<String>?
        ): DialogFragment? {

            val manager = context.packageManager
            val apps = manager.queryIntentActivities(intent, 0)

            if (apps.isNotEmpty()) {
                val appResources = ArrayList<AppInfo>(apps.size)
                val shouldCheckPackages = appsFilter?.isNotEmpty() == true

                for (resolveInfo in apps) {
                    val packageName = resolveInfo.activityInfo.packageName

                    if (shouldCheckPackages && !appsFilter!!.contains(packageName)) {
                        continue
                    }

                    val title = resolveInfo.loadLabel(manager).toString()
                    val name = resolveInfo.activityInfo.name
                    val drawable = resolveInfo.loadIcon(manager)
                    appResources.add(AppInfo(title, packageName, name, drawable))
                }

                val toRemove = ArrayList<AppInfo>()

                toExclude?.takeIf { it.isNotEmpty() }?.let { set ->
                    appResources.forEach {
                        if (set.contains(it.packageName)) toRemove.add(it)
                    }
                }

                if (toRemove.isNotEmpty()) appResources.removeAll(toRemove)

                return Builder(
                    context = context,
                    apps = appResources,
                    shareIntent = intent,
                    title = shareTitle,
                    isGrid = isGrid
                )
                    .create()
            }

            return null
        }

        /**
         * Returns a [BottomSheetMenuDialogFragment] to be used as a share intent like Android 5.x+ Share Intent.
         *
         *
         * An example of an intent to pass is sharing some form of text:<br></br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br></br>
         * intent.setType("text/`*`");<br></br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br></br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br></br>
         * if (bottomSheet != null) bottomSheet.show();<br></br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title for the share intent
         * @param isGrid     If the share intent BottomSheet should be grid styled
         * @param appsFilter If provided share will be limited to contained packaged names
         * @param toExclude  If provided, share will exclude the given package names
         * @return A [BottomSheetMenuDialogFragment] with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            @StringRes shareTitle: Int,
            isGrid: Boolean,
            appsFilter: Set<String>?,
            toExclude: Set<String>?
        ): DialogFragment? {
            return createShareBottomSheet(
                context,
                intent,
                context.getString(shareTitle),
                isGrid,
                appsFilter,
                toExclude
            )
        }

        /**
         * Returns a [BottomSheetMenuDialogFragment] to be used as a share intent like Android 5.x+ Share Intent.
         *
         *
         * An example of an intent to pass is sharing some form of text:<br></br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br></br>
         * intent.setType("text/`*`");<br></br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br></br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br></br>
         * if (bottomSheet != null) bottomSheet.show();<br></br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title string resource for the share intent
         * @param isGrid     If the share intent BottomSheet should be grid styled
         * @return A [BottomSheetMenuDialogFragment] with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            @StringRes shareTitle: Int,
            isGrid: Boolean
        ): DialogFragment? {
            return createShareBottomSheet(
                context,
                intent,
                context.getString(shareTitle),
                isGrid,
                null,
                null
            )
        }

        /**
         * Returns a [BottomSheetMenuDialogFragment] to be used as a share intent like Android 5.x+ Share Intent.
         *
         *
         * An example of an intent to pass is sharing some form of text:<br></br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br></br>
         * intent.setType("text/`*`");<br></br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br></br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br></br>
         * if (bottomSheet != null) bottomSheet.show();<br></br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title for the share intent
         * @param isGrid     If the share intent BottomSheet should be grid styled
         * @return A [BottomSheetMenuDialogFragment] with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            shareTitle: String,
            isGrid: Boolean
        ): DialogFragment? {
            return createShareBottomSheet(context, intent, shareTitle, isGrid, null, null)
        }

        /**
         * Returns a [BottomSheetMenuDialogFragment] to be used as a share intent like Android 5.x+ Share Intent. This will be List styled by default.<br></br>
         * If grid style is desired, use [.createShareBottomSheet]
         *
         *
         * An example of an intent to pass is sharing some form of text:<br></br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br></br>
         * intent.setType("text/`*`");<br></br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br></br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br></br>
         * if (bottomSheet != null) bottomSheet.show();<br></br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title for the share intent
         * @return A [BottomSheetMenuDialogFragment] with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            shareTitle: String
        ): DialogFragment? {
            return createShareBottomSheet(context, intent, shareTitle, false, null, null)
        }

        /**
         * Returns a [BottomSheetMenuDialogFragment] to be used as a share intent like Android 5.x+ Share Intent. This will be list styled by default.<br></br>
         * If grid style is desired, use [.createShareBottomSheet]
         *
         *
         * An example of an intent to pass is sharing some form of text:<br></br>
         * Intent intent = new Intent(Intent.ACTION_SEND);<br></br>
         * intent.setType("text/`*`");<br></br>
         * intent.putExtra(Intent.EXTRA_TEXT, "Some text to share");<br></br>
         * BottomSheet bottomSheet = BottomSheet.createShareBottomSheet(this, intent, "Share");<br></br>
         * if (bottomSheet != null) bottomSheet.show();<br></br>
         *
         * @param context    App context
         * @param intent     Intent to get apps for
         * @param shareTitle The optional title for the share intent
         * @return A [BottomSheetMenuDialogFragment] with the apps that can handle the share intent. NULL maybe returned if no
         * apps can handle the share intent
         */
        @JvmStatic
        fun createShareBottomSheet(
            context: Context,
            intent: Intent,
            @StringRes shareTitle: Int
        ): DialogFragment? {
            return createShareBottomSheet(
                context,
                intent,
                context.getString(shareTitle),
                false,
                null,
                null
            )
        }
    }

    private constructor(builder: Builder) : this() {
        this.builder = builder
        this.listener = builder.listener
    }

    private lateinit var builder: Builder

    private lateinit var container: LinearLayout

    private lateinit var closeContainer: LinearLayout

    private var listener: BottomSheetListener? = null

    private lateinit var adapter: BaseAdapter

    private var dismissEvent = BottomSheetListener.DISMISS_EVENT_MANUAL

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        retainInstance = true
        return BottomSheetDialog(requireActivity(), builder.style).apply {
            setOnShowListener(DialogInterface.OnShowListener {

                if (container.parent == null) return@OnShowListener
                val params =
                    (container.parent as View).layoutParams as CoordinatorLayout.LayoutParams
                val behavior = params.behavior

                // Should always be the case
                if (behavior is BottomSheetBehavior<*>) {
                    if (builder.autoExpand) behavior.state = BottomSheetBehavior.STATE_EXPANDED

                    behavior.addBottomSheetCallback(object :
                        BottomSheetBehavior.BottomSheetCallback() {
                        override fun onStateChanged(bottomSheet: View, state: Int) {
                            if (state == BottomSheetBehavior.STATE_HIDDEN) {
                                dismissEvent = BottomSheetListener.DISMISS_EVENT_SWIPE
                                dismiss()
                            }
                        }

                        override fun onSlide(bottomSheet: View, slideOffSet: Float) {
                            closeContainer.alpha = if (slideOffSet > 0) slideOffSet else 0.0f

                        }
                    })
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Objects.requireNonNull(builder)
        container = view.findViewById(R.id.bottom_sheet_container)
        val title = container.findViewById<TextView>(R.id.bottom_sheet_title)
        val gridView = container.findViewById<GridView>(R.id.bottom_sheet_grid)
        closeContainer = container.findViewById(R.id.bottom_sheet_close_container)
        val closeTitle = closeContainer.findViewById<TextView>(R.id.bottom_sheet_close_title)
        initUi(title, closeTitle, gridView)

        adapter = when {
            builder.menuItems.isNotEmpty() -> {
                GridAdapter(
                    ContextThemeWrapper(requireActivity(), builder.style),
                    builder.menuItems,
                    builder.isGrid
                )
            }

            builder.apps.isNotEmpty() -> {
                AppAdapter(
                    ContextThemeWrapper(requireActivity(), builder.style),
                    builder.apps,
                    builder.isGrid
                )
            }

            else -> throw IllegalStateException("No items were passed to the builder")
        }
        gridView.onItemClickListener = this
        gridView.adapter = adapter
        listener?.onSheetShown(this, builder.`object`)
        this.isCancelable = builder.cancelable

        closeContainer.findViewById<ImageButton>(R.id.bottom_sheet_close).setOnClickListener {
            dismissEvent = BottomSheetListener.DISMISS_EVENT_MANUAL
            dismiss()
        }
    }

    private fun initUi(title: TextView, closeTitle: TextView, gridView: GridView) {
        val hasTitle = !TextUtils.isEmpty(builder.title)

        if (hasTitle) {
            title.text = builder.title
        } else {
            title.visibility = View.GONE
        }

        if (!builder.isGrid) {
            val padding = resources.getDimensionPixelSize(R.dimen.bottom_sheet_menu_list_padding)
            gridView.setPadding(0, if (hasTitle) 0 else padding, 0, padding)
        }

        if (!TextUtils.isEmpty(builder.closeTitle)) {
            closeTitle.text = builder.closeTitle
        } else {
            closeTitle.visibility = View.GONE
        }

        gridView.numColumns = getNumberColumns()
    }

    private fun getNumberColumns(): Int {
        if (builder.columnCount > 0) return builder.columnCount
        val isTablet = resources.getBoolean(R.bool.bottom_sheet_menu_it_tablet)

        val numItems = builder.menuItems.size

        if (builder.isGrid) {
            // Show 4 columns if a tablet and the number of its is 4 or >=7
            return if ((numItems >= 7 || numItems == GRID_MAX_COLUMN) && isTablet) {
                GRID_MAX_COLUMN
            } else {
                GRID_MIN_COLUMNS
            }
        }

        return when (isTablet) {
            // If a tablet with more than 6 items are present, split them into 2 columns
            true -> if (numItems >= MIN_LIST_TABLET_ITEMS) 2 else 1
            // Regular phone, one column
            else -> 1
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onSheetDismissed(this, builder.`object`, dismissEvent)
        super.onDismiss(dialog)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        dismissEvent = BottomSheetListener.DISMISS_EVENT_ITEM_SELECTED

        if (adapter is GridAdapter) {
            if (listener != null) {
                val item = (adapter as GridAdapter).getItem(position)
                listener?.onSheetItemSelected(this, item, builder.`object`)
                dismiss()
            }
        } else if (adapter is AppAdapter) {
            val appInfo = (adapter as AppAdapter).getItem(position)
            val intent = Intent(builder.shareIntent)
            intent.component = ComponentName(appInfo.packageName, appInfo.name)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            requireContext().startActivity(intent)
        }
    }

    /**
     * Builder factory used for creating [BottomSheetMenuDialogFragment]
     */
    class Builder @JvmOverloads constructor(
        private val context: Context,
        @StyleRes style: Int = R.style.Theme_BottomSheetMenuDialog_Light,
        columnCount: Int = -1,
        @MenuRes sheet: Int = -1,
        cancelable: Boolean = true,
        isGrid: Boolean = false,
        autoExpand: Boolean = false,
        menuItems: MutableList<MenuItem> = mutableListOf(),
        apps: MutableList<AppInfo> = mutableListOf(),
        title: String? = null,
        closeTitle: String? = null,
        shareIntent: Intent? = null,
        listener: BottomSheetListener? = null,
        `object`: Any? = null,
        idsToDisable: Array<Int>? = null
    ) {

        var style: Int = style; private set
        var columnCount: Int = columnCount; private set
        var title: String? = title; private set
        var cancelable: Boolean = cancelable; private set
        var isGrid: Boolean = isGrid; private set
        var autoExpand: Boolean = autoExpand; private set
        var menuItems: MutableList<MenuItem> = menuItems; private set
        var apps: MutableList<AppInfo> = apps; private set
        var shareIntent: Intent? = shareIntent; private set
        var listener: BottomSheetListener? = listener; private set
        var `object`: Any? = `object`; private set
        var closeTitle: String? = closeTitle; private set

        init {
            if (sheet != -1 && menuItems.isEmpty()) setSheet(sheet, idsToDisable)
        }

        /**
         * Sets the [BottomSheetMenuDialogFragment] to use a dark theme
         *
         * @return
         */
        fun dark(): Builder {
            style = R.style.Theme_BottomSheetMenuDialog
            return this
        }

        /**
         * Sets the [BottomSheetMenuDialogFragment] to use the DayNight theme
         *
         * @return
         */
        fun dayNight(): Builder {
            style = R.style.Theme_BottomSheetMenuDialog_DayNight
            return this
        }

        /**
         * Sets the style of the [BottomSheetMenuDialogFragment]
         *
         * @param style
         * @return
         */
        fun setStyle(@StyleRes style: Int): Builder {
            this.style = style
            return this
        }

        /**
         * Sets the title of the [BottomSheetMenuDialogFragment]
         *
         * @param title String for the title
         * @return
         */
        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        /**
         * Sets the title of the [BottomSheetMenuDialogFragment]
         *
         * @param title String resource for the title
         * @return
         */
        fun setTitle(@StringRes title: Int): Builder {
            return setTitle(context.getString(title))
        }

        /**
         * Sets the [BottomSheetMenuDialogFragment] to use a grid for displaying options
         *
         * @return
         */
        fun grid(): Builder {
            isGrid = true
            return this
        }

        /**
         * Sets whether the [BottomSheetMenuDialogFragment] is cancelable with the [BACK][KeyEvent.KEYCODE_BACK] key.
         *
         * @param cancelable If the dialog can be canceled
         * @return
         */
        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        /**
         * Sets the [BottomSheetListener] to receive callbacks
         *
         * @param listener The [BottomSheetListener] to receive callbacks for
         * @return
         */
        fun setListener(listener: BottomSheetListener): Builder {
            this.listener = listener
            return this
        }

        /**
         * Sets the menu resource to use for the [BottomSheetMenuDialogFragment]
         *
         * @param sheetItems The [BottomSheetListener] to receive callbacks for
         * @param idsToDisable Ids of any MenuItems to set disabled
         * @return
         */
        fun setSheet(@MenuRes sheetItems: Int, idsToDisable: Array<Int>?): Builder {
            val menu = BottomSheetMenu(context)
            MenuInflater(context).inflate(sheetItems, menu)

            idsToDisable?.let {
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (it.contains(item.itemId)) item.isEnabled = false
                }
            }
            return setMenu(menu)
        }

        /**
         * Sets the menu resource to use for the [BottomSheetMenuDialogFragment]
         *
         * @param sheetItems The [BottomSheetListener] to receive callbacks for
         * @return
         */
        fun setSheet(@MenuRes sheetItems: Int): Builder = setSheet(sheetItems, null)

        /**
         * Sets the menu to use for the [BottomSheetMenuDialogFragment]
         *
         * @param menu
         * @return
         */
        fun setMenu(menu: Menu): Builder {
            val items = ArrayList<MenuItem>(menu.size())

            for (i in 0 until menu.size()) {
                items.add(menu.getItem(i))
            }

            return setMenuItems(items)

            return this
        }

        /**
         * Adds the [List] of menu items to use for the [BottomSheetMenuDialogFragment]
         *
         * @param menuItems
         * @return
         */
        fun setMenuItems(menuItems: List<MenuItem>): Builder {
            this.menuItems.addAll(menuItems)
            return this
        }

        /**
         * Adds a [MenuItem] to the [BottomSheetMenuDialogFragment]. For creating a [MenuItem], see [BottomSheetMenuItem]
         *
         * @param item
         * @return
         */
        fun addMenuItem(item: MenuItem): Builder {
            menuItems.add(item)
            return this
        }

        /**
         * Sets the number of columns that will be shown when set to a grid style
         *
         * @param columnCount Number of columns to show
         * @return
         */
        fun setColumnCount(columnCount: Int): Builder {
            this.columnCount = columnCount
            return this
        }

        /**
         * Sets the number of columns that will be shown when set to a grid style
         *
         * @param columnCount Integer resource containing number of columns to show
         * @return
         */
        fun setColumnCountResource(@IntegerRes columnCount: Int): Builder {
            return setColumnCount(context.resources.getInteger(columnCount))
        }

        /**
         * Sets the [Object] to be passed with the [BottomSheetMenuDialogFragment]
         *
         * @param object Optional [Object]
         * @return
         */
        fun `object`(`object`: Any?): Builder {
            this.`object` = `object`
            return this
        }

        /**
         * Sets if the [BottomSheetMenuDialogFragment] should auto expand when opened. Default value is true
         */
        fun setAutoExpand(autoExpand: Boolean): Builder {
            this.autoExpand = autoExpand
            return this
        }

        /**
         * Sets the close title of the [BottomSheetMenuDialogFragment]
         *
         * @param closeTitle The text to be used for the close title
         */
        fun setCloseTitle(closeTitle: String): Builder {
            this.closeTitle = closeTitle
            return this
        }

        /**
         * Sets the close title of the [BottomSheetMenuDialogFragment]
         *
         * @param closeTitle The text resource to be used for the close title
         */
        fun setCloseTitle(@StringRes closeTitle: Int): Builder {
            return setCloseTitle(context.getString(closeTitle))
        }

        /**
         * Sets the apps to be used for a share intent. This is not a public facing method.
         *
         *
         * See [BottomSheetMenuDialogFragment.createShareBottomSheet] for creating a share intent [BottomSheetMenuDialogFragment]
         *
         * @param apps   List of apps to use in the share intent
         * @param intent The [Intent] used for creating the share intent
         * @return
         */
        private fun setApps(apps: List<AppInfo>, intent: Intent): Builder {
            this.apps.clear()
            this.apps.addAll(apps)
            shareIntent = intent
            return this
        }

        /**
         * Creates the [BottomSheetMenuDialogFragment] but does not show it.
         *
         * @return
         */
        fun create(): BottomSheetMenuDialogFragment {
            return BottomSheetMenuDialogFragment(this)
        }

        /**
         * Creates the [BottomSheetMenuDialogFragment] and shows it.
         *
         * @param manager @link FragmentManager} the [BottomSheetMenuDialogFragment] will be added to
         * @param tag     Optional tag for the [BottomSheetDialogFragment]
         */
        @JvmOverloads
        fun show(manager: FragmentManager, tag: String? = null) {
            create().show(manager, tag)
        }
    }
}

private const val MIN_LIST_TABLET_ITEMS = 6

private const val GRID_MIN_COLUMNS = 3

private const val GRID_MAX_COLUMN = 4
