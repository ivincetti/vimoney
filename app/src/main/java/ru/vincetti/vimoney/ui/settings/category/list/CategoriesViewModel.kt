package ru.vincetti.vimoney.ui.settings.category.list

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.vimoney.data.repository.CategoryRepo
import ru.vincetti.vimoney.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    categoryRepo: CategoryRepo
) : ViewModel() {

    val needNavigate2Home = SingleLiveEvent<Boolean>()
    val needNavigate2AddCategory = SingleLiveEvent<Boolean>()
    val needNavigate2Check = SingleLiveEvent<Int>()

    val categories = categoryRepo.loadAllObservable()

    fun backButtonClicked() {
        needNavigate2Home.value = true
    }

    fun addCategoryClicked() {
        needNavigate2AddCategory.value = true
    }

    fun clickOnElement(id: Int) {
        if (id > 1) needNavigate2Check.value = id
    }
}
