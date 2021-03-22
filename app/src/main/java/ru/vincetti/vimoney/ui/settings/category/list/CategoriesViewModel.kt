package ru.vincetti.vimoney.ui.settings.category.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.vincetti.modules.core.utils.SingleLiveEvent
import ru.vincetti.vimoney.models.CategoriesModel
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    categoriesModel: CategoriesModel
) : ViewModel() {

    private val _needNavigate2Home = SingleLiveEvent<Unit>()
    val needNavigate2Home: LiveData<Unit>
        get() = _needNavigate2Home

    private val _needNavigate2AddCategory = SingleLiveEvent<Unit>()
    val needNavigate2AddCategory: LiveData<Unit>
        get() = _needNavigate2AddCategory

    private val _needNavigate2Check = SingleLiveEvent<Int>()
    val needNavigate2Check: LiveData<Int>
        get() = _needNavigate2Check

    val categories = categoriesModel.getAllCategories()

    fun backButtonClicked() {
        _needNavigate2Home.value = Unit
    }

    fun addCategoryClicked() {
        _needNavigate2AddCategory.value = Unit
    }

    fun clickOnElement(id: Int) {
        if (id > 1) _needNavigate2Check.value = id
    }
}
