package ru.vincetti.vimoney.ui.pin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_pin.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.ui.login.SharedPinViewModel

class PinFragment : Fragment(R.layout.fragment_pin) {

    private val viewModel: PinViewModel by viewModels()
    private val sharedViewModel: SharedPinViewModel by activityViewModels()

    private val keyPadListener = object : PinKeyboardViewListener {
        override fun onPinKeyUpdated(count: Int) {
            fragment_pin_display.updateDisplay(count)
        }

        override fun onPinSet(pin: String) {
            viewModel.pinSet(pin)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewsInit()
        observersInit()
    }

    private fun viewsInit() {
        fragment_pin_keypad.listener = keyPadListener
    }

    private fun observersInit() {
        viewModel.pin.observe(viewLifecycleOwner) {
            sharedViewModel.pinSet(it)
        }
        sharedViewModel.need2ReenterPin.observe(viewLifecycleOwner) {
            if (it) {
                fragment_pin_keypad.reset()
                sharedViewModel.need2ReenterPinProcessed()
            }

        }
        sharedViewModel.need2CheckPin.observe(viewLifecycleOwner) {
            if (it) {
                fragment_pin_keypad.reset()
                sharedViewModel.need2RCheckPinProcessed()
            }
        }
    }
}
