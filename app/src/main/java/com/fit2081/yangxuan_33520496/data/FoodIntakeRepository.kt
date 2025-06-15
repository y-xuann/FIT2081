package com.fit2081.yangxuan_33520496.data

import android.content.Context

class FoodIntakeRepository {
    var foodIntakeDao: FoodIntakeDao

    constructor(context: Context) {
        // Get the PatientDao instance from the HospitalDatabase.
        foodIntakeDao = UserDatabase.getDatabase(context).foodIntakeDao()
    }

    suspend fun insert(foodIntake: FoodIntake) {
        foodIntakeDao.insertFoodIntake(foodIntake)
    }
    suspend fun updateFoodIntake(foodIntake: FoodIntake) {
        foodIntakeDao.updateFoodIntake(foodIntake)
    }
    suspend fun getByUser(userId: String): FoodIntake? {
        return foodIntakeDao.getByUser(userId)
    }

}
