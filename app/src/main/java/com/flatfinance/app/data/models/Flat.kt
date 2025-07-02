package com.flatfinance.app.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flats")
data class Flat(
    @PrimaryKey
    val id: String,
    val name: String,
    val code: String,
    val creatorId: String,
    val memberIds: List<String> = emptyList(),
    val rent: Double? = null,
    val rentDueDay: Int? = null,
    val electricityCap: Double? = null,
    val wifiBill: Double? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)