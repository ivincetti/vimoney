package ru.vincetti.vimoney.ui.pin

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import ru.vincetti.vimoney.databinding.ViewPinKeyboardBinding

class PinKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var listener: PinKeyboardViewListener? = null

    private val pinSaver = PinSaver()

    private var binding =
        ViewPinKeyboardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.btnLogin1.setOnClickListener { setKey(PinKey.ONE.key) }
        binding.btnLogin2.setOnClickListener { setKey(PinKey.TWO.key) }
        binding.btnLogin3.setOnClickListener { setKey(PinKey.THREE.key) }
        binding.btnLogin4.setOnClickListener { setKey(PinKey.FOUR.key) }
        binding.btnLogin5.setOnClickListener { setKey(PinKey.FIVE.key) }
        binding.btnLogin6.setOnClickListener { setKey(PinKey.SIX.key) }
        binding.btnLogin7.setOnClickListener { setKey(PinKey.SEVEN.key) }
        binding.btnLogin8.setOnClickListener { setKey(PinKey.EIGHT.key) }
        binding.btnLogin9.setOnClickListener { setKey(PinKey.NINE.key) }
        binding.btnLogin0.setOnClickListener { setKey(PinKey.ZERO.key) }
        binding.btnLoginDelete.setOnClickListener { deleteSingleKey() }
    }

    fun reset(){
        pinSaver.reset()
        updateDisplay()
    }

    private fun setKey(key: String) {
        pinSaver.addKey(key)
        updateDisplay()
        if (pinSaver.isPinSet) listener?.onPinSet(pinSaver.pin)
    }

    private fun deleteSingleKey() {
        pinSaver.deleteSingleKey()
        updateDisplay()
    }

    private fun updateDisplay() {
        listener?.onPinKeyUpdated(pinSaver.length)
    }
}

interface PinKeyboardViewListener {

    fun onPinSet(pin: String)

    fun onPinKeyUpdated(count: Int)
}
