package ru.vincetti.modules.usecases.category

import androidx.lifecycle.LiveData
import ru.vincetti.modules.core.models.Category
import ru.vincetti.modules.database.repository.CategoryRepo
import javax.inject.Inject

interface GetAllCategoriesUseCase {

    operator fun invoke(): LiveData<List<Category>>
}

class GetAllCategoriesUseCaseImpl @Inject constructor(
    private val categoryRepo: CategoryRepo,
) : GetAllCategoriesUseCase {

    override operator fun invoke(): LiveData<List<Category>> {
        return categoryRepo.loadAllLive()
    }
}
