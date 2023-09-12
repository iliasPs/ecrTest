package com.example.ecrtool.db

import androidx.room.TypeConverter

class DoubleTypeConverter {
    @TypeConverter
    fun toDouble(value: Double): Double {
        return value
    }
}