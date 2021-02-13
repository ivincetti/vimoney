package ru.vincetti.vimoney.ui.transaction.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.vincetti.modules.core.models.Category
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.DialogCategoryGridBinding
import ru.vincetti.vimoney.ui.transaction.main.categorylist.CategoryListAdapter

class CategoryListDialog : DialogFragment() {

    private var categories: List<Category>? = null

    private var _binding: DialogCategoryGridBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AlertDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCategoryGridBinding.inflate(layoutInflater)
        return binding.root
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
            binding.categoriesSymbolsListRecycleView.adapter = categoriesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setList(categories: List<Category>) {
        this.categories = categories
    }
}
