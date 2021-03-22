package ru.vincetti.modules.core.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatesFormat {

    fun getMonthName(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ofPattern("LLLL"))
    }

    fun getMonth00(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ofPattern("MM"))
    }

    fun getYear0000(localDate: LocalDate): String {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy"))
    }
}
