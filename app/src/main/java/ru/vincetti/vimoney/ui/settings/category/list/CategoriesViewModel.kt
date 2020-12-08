package ru.vincetti.vimoney.ui.settings.category.list

import androidx.lifecycle.*
import ru.vincetti.vimoney.data.repository.CategoryRepo

class CategoriesViewModel(categoryRepo: CategoryRepo) : ViewModel() {

    private var _need2Navigate2Home = MutableLiveData<Boolean>()
    val need2Navigate2Home: LiveData<Boolean>
        get() = _need2Navigate2Home

    private var _need2Navigate2AddCategory = MutableLiveData<Boolean>()
    val need2Navigate2AddCategory: LiveData<Boolean>
        get() = _need2Navigate2AddCategory

    val categories = categoryRepo.loadAllObservable()

    init {
        _need2Navigate2Home.value = false
        _need2Navigate2AddCategory.value = false
    }

    fun backButtonClicked() {
        _need2Navigate2Home.value = true
    }

    fun navigated2Home() {
        _need2Navigate2Home.value = false
    }

    fun addCategoryClicked() {
        _need2Navigate2AddCategory.value = true
    }

    fun navigated2AddCategory() {
        _need2Navigate2AddCategory.value = false
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
