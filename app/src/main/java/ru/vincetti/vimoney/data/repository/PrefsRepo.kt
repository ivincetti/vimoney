package ru.vincetti.vimoney.data.repository

import android.content.Context

class PrefsRepo(context: Context) {

    companion object {
        private const val DEFAULT_PREFS_REPO = "DEFAULT_PREFS_REPO"
        private const val PIN_CODE_KEY = "PIN_CODE_KEY"
        private const val PIN_CODE_DEFAULT_VALUE = ""
    }

    private val pref = context.getSharedPreferences(DEFAULT_PREFS_REPO, Context.MODE_PRIVATE)

    var pin
        get() = pref.getString(PIN_CODE_KEY, PIN_CODE_DEFAULT_VALUE)
        set(value) {
            pref.edit().apply {
                putString(PIN_CODE_KEY, value)
                apply()
            }
        }

    val isPinSet
        get() = pin != PIN_CODE_DEFAULT_VALUE

    fun resetPin() {
        pref.edit().apply {
            putString(PIN_CODE_KEY, PIN_CODE_DEFAULT_VALUE)
            apply()
        }
    }
}
