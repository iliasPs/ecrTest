package com.example.ecrtool.db

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun toTimestamp(dateTime: LocalDateTime?): Long? {
        return dateTime?.toEpochSecond(ZoneOffset.UTC)
    }
}