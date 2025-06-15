package com.fit2081.yangxuan_33520496.data
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NutriCoachTipsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTip(tip: NutriCoachTips)

    @Query("SELECT * FROM nutricoachtips WHERE clinicianId = :clinicianId ORDER BY timestamp DESC")
    suspend fun getTipsByClinician(clinicianId: String): List<NutriCoachTips>

    @Delete
    suspend fun deleteTip(tip: NutriCoachTips)

}
