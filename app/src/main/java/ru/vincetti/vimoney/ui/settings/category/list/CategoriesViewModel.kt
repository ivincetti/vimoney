package ru.vincetti.vimoney.ui.settings.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.utils.SingleLiveEvent

class CategoriesViewModel(
    categoryRepo: CategoryRepo
) : ViewModel() {

    val need2Navigate2Home = SingleLiveEvent<Boolean>()
    val need2Navigate2AddCategory = SingleLiveEvent<Boolean>()

    val categories = categoryRepo.loadAllObservable()

    fun backButtonClicked() {
        need2Navigate2Home.value = true
    }

    fun addCategoryClicked() {
        need2Navigate2AddCategory.value = true
    }
}

class CategoriesModelFactory(private val repo: CategoryRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
