package ru.vincetti.vimoney.ui.settings.category.list

import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.*
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
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
