package com.kennyc.bottomsheet.model

import android.graphics.drawable.Drawable
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
data class AppInfo(val title: String, val packageName: String, val name: String, val drawable: Drawable)