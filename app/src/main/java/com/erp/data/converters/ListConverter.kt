package com.erp.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converter for Room to store List<String> in the database
 */
class ListConverter {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null || value.isEmpty()) {
            ""
        } else {
            gson.toJson(value)
        }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) {
            return emptyList()
        }
        
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
} 