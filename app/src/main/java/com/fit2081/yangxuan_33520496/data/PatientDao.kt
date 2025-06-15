package com.fit2081.yangxuan_33520496.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patients: List<Patient>)

    @Query("SELECT COUNT(*) FROM patient_table")
    suspend fun getCount(): Int

    @Query("SELECT * FROM patient_table WHERE userId = :userId")
    suspend fun getPatientById(userId: String): Patient?


    @Query("SELECT * FROM patient_table ORDER BY userId ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Insert
    suspend fun insert(patient: Patient)

    @Update
    suspend fun updatePatient(patient: Patient)

    @Query("SELECT * FROM patient_table WHERE phoneNumber = :phone AND userId = :userId LIMIT 1")
    suspend fun validateUser(phone: String, userId: String): Patient?

    @Query("DELETE FROM patient_table")
    suspend fun deleteAllPatients()

    // Get all registered patients (have a password)
    @Query("SELECT * FROM patient_table WHERE password IS NOT NULL")
    suspend fun getRegisteredPatients(): List<Patient>

    // Get all unregistered patients (password is NULL)
    @Query("SELECT * FROM patient_table WHERE password IS NULL")
    suspend fun getUnregisteredPatients(): List<Patient>

    @Query("SELECT AVG(heifaTotalScoreMale) FROM patient_table WHERE sex = 'Male'")
    suspend fun getAverageHeifaMale(): Double?

    @Query("SELECT AVG(heifaTotalScoreFemale) FROM patient_table WHERE sex = 'Female'")
    suspend fun getAverageHeifaFemale(): Double?
}

