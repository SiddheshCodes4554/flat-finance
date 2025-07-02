package com.flatfinance.app.utils

import androidx.room.TypeConverter
import com.flatfinance.app.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return gson.toJson(value ?: emptyList<String>())
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
    
    @TypeConverter
    fun fromExpenseCategory(value: ExpenseCategory?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toExpenseCategory(value: String?): ExpenseCategory? {
        return value?.let { ExpenseCategory.valueOf(it) }
    }
    
    @TypeConverter
    fun fromExpenseType(value: ExpenseType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toExpenseType(value: String?): ExpenseType? {
        return value?.let { ExpenseType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromSplitMethod(value: SplitMethod?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toSplitMethod(value: String?): SplitMethod? {
        return value?.let { SplitMethod.valueOf(it) }
    }
    
    @TypeConverter
    fun fromReminderType(value: ReminderType?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toReminderType(value: String?): ReminderType? {
        return value?.let { ReminderType.valueOf(it) }
    }
    
    @TypeConverter
    fun fromReminderStatus(value: ReminderStatus?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toReminderStatus(value: String?): ReminderStatus? {
        return value?.let { ReminderStatus.valueOf(it) }
    }
    
    @TypeConverter
    fun fromBudgetPeriod(value: BudgetPeriod?): String? {
        return value?.name
    }
    
    @TypeConverter
    fun toBudgetPeriod(value: String?): BudgetPeriod? {
        return value?.let { BudgetPeriod.valueOf(it) }
    }
    
    @TypeConverter
    fun fromMap(value: Map<String, Double>?): String {
        return gson.toJson(value ?: emptyMap<String, Double>())
    }
    
    @TypeConverter
    fun toMap(value: String): Map<String, Double> {
        val mapType = object : TypeToken<Map<String, Double>>() {}.type
        return gson.fromJson(value, mapType) ?: emptyMap()
    }
}