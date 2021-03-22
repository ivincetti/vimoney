package ru.vincetti.vimoney.ui.transaction.main

sealed class TransactionMainContentAction {

    object SumError : TransactionMainContentAction()

    object AccountError : TransactionMainContentAction()

    object CloseSelf : TransactionMainContentAction()
}
