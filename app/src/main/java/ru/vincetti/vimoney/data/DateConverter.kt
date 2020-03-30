package ru.vincetti.vimoney.data

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        timestamp?.let {
            return Date(timestamp)
        }
        return null
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
