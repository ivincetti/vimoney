package ru.vincetti.vimoney.ui.transaction.main

import ru.vincetti.modules.core.models.Transaction

sealed class TransactionMainContentState {

    object Empty : TransactionMainContentState()

    sealed class Filled : TransactionMainContentState() {

        abstract val update: Boolean

        abstract val transaction: Transaction

        class Income(
            override val update: Boolean,
            override val transaction: Transaction,
        ) : Filled()

        class Spending(
            override val update: Boolean,
            override val transaction: Transaction,
        ) : Filled()

        class Transfer(
            override val update: Boolean,
            override val transaction: Transaction,
            val nestedTransaction: Transaction,
        ) : Filled()
    }
}
