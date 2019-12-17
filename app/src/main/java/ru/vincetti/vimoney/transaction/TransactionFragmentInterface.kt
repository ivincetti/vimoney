package ru.vincetti.vimoney.transaction

import android.view.View

interface TransactionFragmentInterface {
    // views initialization
    fun initFragmentViews(view: View)

    fun initFragmentLogic()
    // save transaction logic
    fun setTypeAction()
}
