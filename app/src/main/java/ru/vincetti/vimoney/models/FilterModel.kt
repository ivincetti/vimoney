package ru.vincetti.vimoney.models

import ru.vincetti.modules.core.models.Account
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.repository.AccountRepo
import ru.vincetti.modules.database.repository.CategoryRepo
import javax.inject.Inject

class FilterModel @Inject constructor(
    private val accountRepo: AccountRepo,
    private val categoryRepo: CategoryRepo,
) {

    suspend fun loadAccountById(id: Int): Account? {
        return accountRepo.loadById(id)
    }

    suspend fun loadCategoryById(id: Int): Category? {
        return categoryRepo.loadById(id)
    }
}
