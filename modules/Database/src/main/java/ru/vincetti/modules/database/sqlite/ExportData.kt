package ru.vincetti.modules.database.sqlite

import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.TransactionRepo

object ExportData {

    suspend fun export(
        transactionRepo: TransactionRepo,
        accountRepo: AccountRepo,
        categoryRepo: CategoryRepo
    ) = Triple(
        transactionRepo.loadAll(),
        requireNotNull(accountRepo.loadAll()),
        requireNotNull(categoryRepo.loadAll())
    )
}
