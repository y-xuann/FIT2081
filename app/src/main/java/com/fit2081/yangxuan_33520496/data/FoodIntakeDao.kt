package com.fit2081.yangxuan_33520496.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodIntake(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intake WHERE userId = :userId LIMIT 1")
    suspend fun getByUser(userId: String): FoodIntake?

    @Query("DELETE FROM food_intake WHERE userId = :userId")
    suspend fun deleteByUser(userId: String)

    @Update
    suspend fun updateFoodIntake(foodIntake: FoodIntake)

}



