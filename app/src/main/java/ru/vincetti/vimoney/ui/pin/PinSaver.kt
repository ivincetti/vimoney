package ru.vincetti.vimoney.ui.pin

class PinSaver {

    companion object {
        private const val PIN_LENGTH = 4
    }

    private var _pin = StringBuilder()
    val pin
        get() = _pin.toString()

    val isPinSet
        get() = pin.length == PIN_LENGTH

    val length
        get() = pin.length

    fun addKey(key: String) {
        _pin.append(key)
    }

    fun deleteSingleKey() {
        if (_pin.isEmpty()) return
        _pin.deleteAt(pin.length - 1)
    }

    fun reset() {
        _pin.clear()
    }
}
