package ru.vincetti.modules.core.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatesFormat {

    fun getMonthName(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("LLLL").format(localDate)
    }

    fun getMonth00(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("MM").format(localDate)
    }

    fun getYear0000(localDate: LocalDate): String {
        return DateTimeFormatter.ofPattern("yyyy").format(localDate)
    }
}
