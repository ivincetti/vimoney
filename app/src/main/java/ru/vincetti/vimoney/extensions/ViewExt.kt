package ru.vincetti.vimoney.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.*

/** Extension to update view margin. */
fun View.updateMargin(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) = updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMargins(left, top, right, bottom) }
