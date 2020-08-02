package ru.vincetti.vimoney.ui.history.filter

import android.content.Intent
import android.os.Bundle
import java.text.DateFormat
import java.util.*

class Filter {

    var accountID = 0
        set(value) {
            if (value > 0) field = value
        }

    var categoryID = 0
        set(value) {
            if (value > 0) field = value
        }

    var count = 0
        set(value) {
            if (value > 0) field = value
        }

    var sumFrom = 0
        set(value) {
            if (value > 0) field = value
        }

    var comment: String? = null
        set(value) {
            if (value != null) field = value
        }

    var dateFrom: Date? = null
        set(value) {
            if (value != null) field = value
        }

    var dateTo: Date? = null
        set(value) {
            if (value != null) field = value
        }

    fun createBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(BUNDLE_FILTER_COUNT_NAME, count)
        bundle.putInt(BUNDLE_FILTER_CHECK_ID_NAME, accountID)
        bundle.putInt(BUNDLE_FILTER_CATEGORY_ID_NAME, categoryID)
        bundle.putInt(BUNDLE_FILTER_SUM_ID_NAME, sumFrom)
        bundle.putString(BUNDLE_FILTER_COMMENT_ID_NAME, comment)
        dateFrom?.let { bundle.putLong(BUNDLE_FILTER_DATE_FROM_ID_NAME, it.time) }
        dateTo?.let { bundle.putLong(BUNDLE_FILTER_DATE_TO_ID_NAME, it.time) }
        return bundle
    }

    fun createIntent(): Intent {
        val intent = Intent()
        intent.putExtra(BUNDLE_FILTER_COUNT_NAME, count)
        intent.putExtra(BUNDLE_FILTER_CHECK_ID_NAME, accountID)
        intent.putExtra(BUNDLE_FILTER_CATEGORY_ID_NAME, categoryID)
        intent.putExtra(BUNDLE_FILTER_SUM_ID_NAME, sumFrom)
        intent.putExtra(BUNDLE_FILTER_COMMENT_ID_NAME, comment)
        dateFrom?.let { intent.putExtra(BUNDLE_FILTER_DATE_FROM_ID_NAME, it.time) }
        dateTo?.let { intent.putExtra(BUNDLE_FILTER_DATE_TO_ID_NAME, it.time) }
        return intent
    }

    override fun toString(): String {
        return """
            count $count
            account $accountID
            category $categoryID
            sum $sumFrom
            comment $comment
            dateFrom ${dateFrom?.let{DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)}}
            dateTo ${dateTo?.let{DateFormat.getDateInstance(DateFormat.MEDIUM).format(it)}}
            """
    }

    companion object {
        private const val BUNDLE_FILTER_COUNT_NAME = "ru.vincetti.vimoney.filter.count"
        private const val BUNDLE_FILTER_CHECK_ID_NAME = "ru.vincetti.vimoney.filter.check_id"
        private const val BUNDLE_FILTER_CATEGORY_ID_NAME = "ru.vincetti.vimoney.filter.category_id"
        private const val BUNDLE_FILTER_SUM_ID_NAME = "ru.vincetti.vimoney.filter.sum_id"
        private const val BUNDLE_FILTER_COMMENT_ID_NAME = "ru.vincetti.vimoney.filter.comment_id"
        private const val BUNDLE_FILTER_DATE_FROM_ID_NAME = "ru.vincetti.vimoney.filter.date_from_id"
        private const val BUNDLE_FILTER_DATE_TO_ID_NAME = "ru.vincetti.vimoney.filter.date_to_id"

        fun createFromBundle(bundle: Bundle): Filter {
            val filter = Filter()
            filter.count = bundle.getInt(BUNDLE_FILTER_COUNT_NAME)
            filter.accountID = bundle.getInt(BUNDLE_FILTER_CHECK_ID_NAME)
            filter.categoryID = bundle.getInt(BUNDLE_FILTER_CATEGORY_ID_NAME)
            filter.sumFrom = bundle.getInt(BUNDLE_FILTER_SUM_ID_NAME)
            filter.comment = bundle.getString(BUNDLE_FILTER_COMMENT_ID_NAME)
            bundle.getLong(BUNDLE_FILTER_DATE_FROM_ID_NAME).let {
                if (it > 0) filter.dateFrom = Date().apply {
                    time = it
                }
            }
            bundle.getLong(BUNDLE_FILTER_DATE_TO_ID_NAME).let {
                if (it > 0) filter.dateTo = Date().apply {
                    time = it
                }
            }
            return filter
        }

        fun createFromIntent(intent: Intent): Filter {
            val filter = Filter()
            filter.count = intent.getIntExtra(BUNDLE_FILTER_COUNT_NAME, 0)
            filter.accountID = intent.getIntExtra(BUNDLE_FILTER_CHECK_ID_NAME, 0)
            filter.categoryID = intent.getIntExtra(BUNDLE_FILTER_CATEGORY_ID_NAME, 0)
            filter.sumFrom = intent.getIntExtra(BUNDLE_FILTER_SUM_ID_NAME, 0)
            filter.comment = intent.getStringExtra(BUNDLE_FILTER_COMMENT_ID_NAME)
            intent.getLongExtra(BUNDLE_FILTER_DATE_FROM_ID_NAME, 0).let {
                if (it > 0) filter.dateFrom = Date().apply {
                    time = it
                }
            }
            intent.getLongExtra(BUNDLE_FILTER_DATE_TO_ID_NAME, 0).let {
                if (it > 0) filter.dateTo = Date().apply {
                    time = it
                }
            }
            return filter
        }
    }
}
