package ru.vincetti.vimoney.extensions

import android.os.Build
import androidx.core.view.WindowInsetsCompat

fun WindowInsetsCompat.top(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    this.getInsets(WindowInsetsCompat.Type.statusBars()).top
} else {
    @Suppress("DEPRECATION")
    this.systemWindowInsetTop
}

fun WindowInsetsCompat.bottom(): Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    this.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
} else {
    @Suppress("DEPRECATION")
    this.systemWindowInsetBottom
}
