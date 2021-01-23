package ru.vincetti.vimoney.ui.settings.category.symbol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import ru.vincetti.vimoney.R
import ru.vincetti.vimoney.databinding.DialogCategoryListBinding

class CategorySymbolListDialog : DialogFragment() {

    private val categories = Category.values().map {
        it.symbol
    }

    private var _binding: DialogCategoryListBinding? = null
    private val binding
        get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AlertDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogCategoryListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesAdapter = CategorySymbolListAdapter(categories) {
            targetFragment?.onActivityResult(
                targetRequestCode,
                it,
                requireActivity().intent
            )
            dismiss()
        }
        binding.categoriesSymbolsListRecycleView.adapter = categoriesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
