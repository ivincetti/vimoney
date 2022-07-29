package ru.vincetti.vimoney.ui.transaction.main

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import ru.vincetti.modules.core.models.AccountList
import ru.vincetti.vimoney.R
import java.util.*

object TransactionFragmentUtils {

    fun showListPopUp(
        context: Context,
        view: View,
        list: List<AccountList>,
        onSuccess: (Int) -> Unit
    ) {
        val popUp = PopupMenu(context, view)
        for (acc in list) {
            popUp.menu.add(Menu.NONE, acc.id, acc.id, acc.name)
        }
        popUp.setOnMenuItemClickListener {
            onSuccess(it.itemId)
            true
        }
        popUp.show()
    }

    fun showKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        activity.currentFocus?.let {
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    fun showNoSumToast(context: Context) {
        showLongToast(context, R.string.add_check_no_sum_warning)
    }

    fun showNoAccountToast(context: Context) {
        showLongToast(context, R.string.add_check_no_account_warning)
    }

    fun showDateDialog(
        context: Context,
        date: Date,
        onPickDate: (Date) -> Unit
    ) {
        val calendar = GregorianCalendar()
        calendar.time = date

        DatePickerDialog(
            context,
            { _, year, month, day ->
                onPickDate(GregorianCalendar(year, month, day).time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showLongToast(context: Context, @StringRes stringRes: Int) {
        Toast.makeText(
            context,
            context.getString(stringRes),
            Toast.LENGTH_SHORT
        ).show()
    }
}
