package ru.vincetti.vimoney.ui.pin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.ViewPinDisplayBinding

class PinDisplayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding =
        ViewPinDisplayBinding.inflate(LayoutInflater.from(context), this, true)

    private val pinDisplays = listOf(
        binding.pinDisplay1,
        binding.pinDisplay2,
        binding.pinDisplay3,
        binding.pinDisplay4
    )

    fun updateDisplay(showKeyNumbers: Int) {
        pinDisplays.forEach { it.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryBackground)) }
        for (i in 0 until showKeyNumbers) {
            pinDisplays[i].setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryText))
        }
    }
}
