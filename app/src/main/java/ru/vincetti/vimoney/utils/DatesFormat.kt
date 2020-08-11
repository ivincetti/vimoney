package ru.vincetti.vimoney.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatesFormat {

    @JvmStatic
    fun getMonthName(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("MMMM").format(localDate)
    }

    @JvmStatic
    fun getMonth00(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("MM").format(localDate)
    }

    @JvmStatic
    fun getYear0000(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("yyyy").format(localDate)
    }
}