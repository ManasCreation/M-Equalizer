package com.example.data.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromIntList(list: List<Int>?): String {
        if (list == null || list.isEmpty()) return "0,0,0,0,0,0,0,0,0,0"
        return list.joinToString(",")
    }

    @TypeConverter
    fun toIntList(data: String?): List<Int> {
        if (data.isNullOrBlank()) return listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        return try {
            val items = data.split(",").mapNotNull { it.trim().toIntOrNull() }
            if (items.size == 10) items
            else (items + List(10) { 0 }).take(10)
        } catch (_: Exception) {
            listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        }
    }
}
