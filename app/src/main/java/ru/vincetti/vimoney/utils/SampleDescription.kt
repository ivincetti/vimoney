package ru.vincetti.vimoney.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.vincetti.vimoney.R
import javax.inject.Inject

class SampleDescription @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun get(): String {
        return context.getString(R.string.transaction_import_sample_desc)
    }
}
