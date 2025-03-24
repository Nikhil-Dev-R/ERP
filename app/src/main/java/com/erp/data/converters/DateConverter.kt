package com.erp.data.converters

import androidx.room.TypeConverter
import com.erp.modules.finance.data.model.InvoiceItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toString()
    }
    
    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }
    
    @TypeConverter
    fun fromInvoiceItemList(value: List<InvoiceItem>?): String? {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toInvoiceItemList(value: String?): List<InvoiceItem>? {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<InvoiceItem>>() {}.type
        return Gson().fromJson(value, listType)
    }
} 