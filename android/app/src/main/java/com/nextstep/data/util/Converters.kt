package com.nextstep.data.util

import androidx.room.TypeConverter
import java.util.Date

/**
 * Room数据库类型转换器
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): List<String> {
        return value?.split(",")?.filter { it.isNotEmpty() } ?: listOf()
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(",")
    }
} 