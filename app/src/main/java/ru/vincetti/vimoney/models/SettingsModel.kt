package ru.vincetti.vimoney.models

import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import ru.vincetti.modules.database.repository.TransactionRepo
import ru.vincetti.modules.database.sqlite.ExportData
import ru.vincetti.modules.database.sqlite.ImportData
import ru.vincetti.modules.files.JsonFile
import javax.inject.Inject

class SettingsModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val transactionRepo: TransactionRepo,
    private val categoryRepo: CategoryRepo,
    private val jsonFile: JsonFile,
) {

    suspend fun export() {
        jsonFile.save(
            ExportData.export(transactionRepo, accountRepo, categoryRepo)
        )
    }

    suspend fun import() {
        ImportData.import(
            transactionRepo,
            accountRepo,
            categoryRepo,
            jsonFile.getData()
        )
    }
}
