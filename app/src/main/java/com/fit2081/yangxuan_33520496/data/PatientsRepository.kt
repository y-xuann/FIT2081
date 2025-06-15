package com.fit2081.yangxuan_33520496.data

import kotlinx.coroutines.flow.first
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

class PatientRepository(private val patientDao: PatientDao) {

    constructor(context: Context) : this(UserDatabase.getDatabase(context).patientDao())

    private val _genderScores = MutableStateFlow<GenderScores?>(null)
    val genderScores: StateFlow<GenderScores?> get() = _genderScores

    suspend fun getAllPatients(): List<Patient> = patientDao.getAllPatients().first()

    suspend fun getGenderSpecificScores(userId: String): GenderScores? {
        val patient = getPatientById(userId)
        val scores = patient?.let {
            val isMale = it.sex.equals("Male", ignoreCase = true)
            GenderScores(
                heifaTotalScore = if (isMale) it.heifaTotalScoreMale else it.heifaTotalScoreFemale,
                discretionaryHeifaScore = if (isMale) it.discretionaryHeifaScoreMale else it.discretionaryHeifaScoreFemale,
                vegetablesHeifaScore = if (isMale) it.vegetablesHeifaScoreMale else it.vegetablesHeifaScoreFemale,
                fruitHeifaScore = if (isMale) it.fruitHeifaScoreMale else it.fruitHeifaScoreFemale,
                grainsAndCerealsHeifaScore = if (isMale) it.grainsAndCerealsHeifaScoreMale else it.grainsAndCerealsHeifaScoreFemale,
                wholegrainsHeifaScore = if (isMale) it.wholegrainsHeifaScoreMale else it.wholegrainsHeifaScoreFemale,
                meatAndAlternativesHeifaScore = if (isMale) it.meatAndAlternativesHeifaScoreMale else it.meatAndAlternativesHeifaScoreFemale,
                dairyAndAlternativesHeifaScore = if (isMale) it.dairyAndAlternativesHeifaScoreMale else it.dairyAndAlternativesHeifaScoreFemale,
                sodiumHeifaScore = if (isMale) it.sodiumHeifaScoreMale else it.sodiumHeifaScoreFemale,
                alcoholHeifaScore = if (isMale) it.alcoholHeifaScoreMale else it.alcoholHeifaScoreFemale,
                waterHeifaScore = if (isMale) it.waterHeifaScoreMale else it.waterHeifaScoreFemale,
                sugarHeifaScore = if (isMale) it.sugarHeifaScoreMale else it.sugarHeifaScoreFemale,
                saturatedFatHeifaScore = if (isMale) it.saturatedFatHeifaScoreMale else it.saturatedFatHeifaScoreFemale,
                unsaturatedFatHeifaScore = if (isMale) it.unsaturatedFatHeifaScoreMale else it.unsaturatedFatHeifaScoreFemale,
            )
        }
        _genderScores.value = scores
        return scores
    }



    suspend fun getPatientById(userId: String): Patient? {
        Log.d("LoginViewModel", patientDao.getPatientById(userId).toString())
        return patientDao.getPatientById(userId)
    }

    // Function to insert a patient into the database.
    suspend fun insert(patient: Patient) {
        // Call the insert function from the PatientDao.
        patientDao.insert(patient)
    }

    val registeredPatients: Flow<List<Patient>> = flow {
        emit(patientDao.getRegisteredPatients())
    }

    val unregisteredPatients: Flow<List<Patient>> = flow {
        emit(patientDao.getUnregisteredPatients())
    }

    suspend fun updatePatient(patient: Patient) {
        patientDao.updatePatient(patient)
    }

    suspend fun loadInitialPatientsFromCSVIfNeeded(context: Context) {
        val existingCount = patientDao.getCount()
        Log.d("PatientRepository", "Existing count: $existingCount")
        if (existingCount > 0) return // Already loaded

        val inputStream = context.assets.open("users_info.csv")
        inputStream.bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line -> // drop header
                val parts = line.split(",")
                if (parts.size >= 31) { // ensure enough columns
                    val patient = Patient(
                        userId = parts[1].trim('"'),
                        phoneNumber = parts[0].trim('"'),
                        name = null,
                        password = null,
                        sex = parts[2].trim('"'),
                        heifaTotalScoreMale = parts[3].trim('"').toDouble(),
                        heifaTotalScoreFemale = parts[4].trim('"').toDouble(),
                        discretionaryHeifaScoreMale = parts[5].trim('"').toDouble(),
                        discretionaryHeifaScoreFemale = parts[6].trim('"').toDouble(),
                        vegetablesHeifaScoreMale = parts[8].trim('"').toDouble(),
                        vegetablesHeifaScoreFemale = parts[9].trim('"').toDouble(),
                        fruitHeifaScoreMale = parts[19].trim('"').toDouble(),
                        fruitHeifaScoreFemale = parts[20].trim('"').toDouble(),
                        fruitServeSize = parts[21].trim('"').toDouble(),
                        fruitVariationScore = parts[22].trim('"').toDouble(),
                        grainsAndCerealsHeifaScoreMale = parts[29].trim('"').toDouble(),
                        grainsAndCerealsHeifaScoreFemale = parts[30].trim('"').toDouble(),
                        wholegrainsHeifaScoreMale = parts[33].trim('"').toDouble(),
                        wholegrainsHeifaScoreFemale = parts[34].trim('"').toDouble(),
                        meatAndAlternativesHeifaScoreMale = parts[36].trim('"').toDouble(),
                        meatAndAlternativesHeifaScoreFemale = parts[37].trim('"').toDouble(),
                        dairyAndAlternativesHeifaScoreMale = parts[40].trim('"').toDouble(),
                        dairyAndAlternativesHeifaScoreFemale = parts[41].trim('"').toDouble(),
                        sodiumHeifaScoreMale = parts[43].trim('"').toDouble(),
                        sodiumHeifaScoreFemale = parts[44].trim('"').toDouble(),
                        alcoholHeifaScoreMale = parts[46].trim('"').toDouble(),
                        alcoholHeifaScoreFemale = parts[47].trim('"').toDouble(),
                        waterHeifaScoreMale = parts[49].trim('"').toDouble(),
                        waterHeifaScoreFemale = parts[50].trim('"').toDouble(),
                        sugarHeifaScoreMale = parts[54].trim('"').toDouble(),
                        sugarHeifaScoreFemale = parts[55].trim('"').toDouble(),
                        saturatedFatHeifaScoreMale = parts[57].trim('"').toDouble(),
                        saturatedFatHeifaScoreFemale = parts[58].trim('"').toDouble(),
                        unsaturatedFatHeifaScoreMale = parts[60].trim('"').toDouble(),
                        unsaturatedFatHeifaScoreFemale = parts[61].trim('"').toDouble()
                    )
                    patientDao.insert(patient)
                }

            }
        }
    }

    suspend fun getAverageHeifaForMale(): Double? {
        return patientDao.getAverageHeifaMale()
    }

    suspend fun getAverageHeifaForFemale(): Double? {
        return patientDao.getAverageHeifaFemale()
    }
}
