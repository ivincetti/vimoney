package ru.vincetti.vimoney.ui.settings.category.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vincetti.vimoney.data.sqlite.CategoryDao

class CategoriesViewModel(categoriesDao: CategoryDao) : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    val categories = categoriesDao.loadAllCategories()

    init {
        _need2Navigate2Home.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }
}

class CategoriesModelFactory(private val dao: CategoryDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
