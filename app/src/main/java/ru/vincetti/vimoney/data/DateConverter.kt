package ru.vincetti.vimoney.data

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    companion object {
        @TypeConverter
        fun toDate(timestamp: Long?): Date? {
            return if (timestamp == null) null
            else Date(timestamp)
        }

        @TypeConverter
        fun toTimestamp(date: Date?): Long? {
            return date?.time
        }
    }
}
