package ru.vincetti.modules.usecases.category

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.repository.CategoryRepo
import javax.inject.Inject

interface GetCategoryByIdUseCase {

    operator fun invoke(categoryId: Int): LiveData<Category?>
}

class GetCategoryByIdUseCaseImpl @Inject constructor(
    private val categoryRepo: CategoryRepo,
) : GetCategoryByIdUseCase {

    override operator fun invoke(categoryId: Int): LiveData<Category?> {
        return categoryRepo.loadByIdLive(categoryId)
    }
}
