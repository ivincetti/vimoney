package ru.vincetti.vimoney.ui.transaction.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_category_list.*
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.data.models.CategoryModel
import ru.vincetti.vimoney.ui.transaction.main.categorylist.CategoryListAdapter

class CategoryListDialog : DialogFragment() {

    private var categories: List<CategoryModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_category_grid, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categories?.let { list ->
            val categoriesAdapter = CategoryListAdapter(list) {
                targetFragment?.onActivityResult(
                    targetRequestCode,
                    list[it].id,
                    requireActivity().intent
                )
                dismiss()
            }
            categories_symbols_list_recycle_view.adapter = categoriesAdapter
        }
    }

    fun setList(categories: List<CategoryModel>) {
        this.categories = categories
    }
}
