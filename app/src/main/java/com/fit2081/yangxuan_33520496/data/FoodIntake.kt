package com.fit2081.yangxuan_33520496.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "food_intake",
    foreignKeys = [ForeignKey(
        entity = Patient::class,
        parentColumns = ["userId"],    // Reference Patient.userId
        childColumns = ["userId"],  // Here in FoodIntake
        onDelete = ForeignKey.CASCADE
    )]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: String,   // Foreign key to Patient.userId
    val persona: String,
    val wakeUpTime: String,
    val sleepTime: String,
    val biggestMealTime: String,
    val foodCategories: String // We'll store as a comma-separated string
)


