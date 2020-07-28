package ru.vincetti.vimoney.ui.settings.category.symbol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_category_list.*
import ru.vincetti.vimoney.R

class CategorySymbolListDialog : DialogFragment() {

    private val categories = Category.values().map {
        it.symbol
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_category_list, null)
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
        categories_symbols_list_recycle_view.adapter = categoriesAdapter
    }
}
