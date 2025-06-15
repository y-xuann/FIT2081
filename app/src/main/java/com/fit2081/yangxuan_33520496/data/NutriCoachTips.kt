package com.fit2081.yangxuan_33520496.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NutriCoachTips")
data class NutriCoachTips(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clinicianId: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
