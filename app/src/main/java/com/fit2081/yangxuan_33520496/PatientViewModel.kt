package com.fit2081.yangxuan_33520496

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fit2081.yangxuan_33520496.data.Patient
import com.fit2081.yangxuan_33520496.data.PatientRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PatientViewModel(context:Context): ViewModel() {
    private val patientRepo = PatientRepository(context)
    val genderScores = patientRepo.genderScores
    private val _name = mutableStateOf("")
    val name: State<String> get() = _name
    // Generative AI model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )
    var dataPatterns by mutableStateOf("")
        private set
    private val _phoneNumber = mutableStateOf("")
    val phoneNumber: State<String> = _phoneNumber

    private val _userId = mutableStateOf("")
    val userId: State<String> = _userId

    private val _isOptimalScore = mutableStateOf<Boolean?>(null)
    val isOptimalScore: State<Boolean?> = _isOptimalScore

    private val _patientLoaded = mutableStateOf(false)
    val patientLoaded: State<Boolean> get() = _patientLoaded

    val registeredPatients = patientRepo.registeredPatients
    val unregisteredPatients = patientRepo.unregisteredPatients

    private val _averageMale = MutableStateFlow("0.0")
    val averageMale: StateFlow<String> = _averageMale

    private val _averageFemale = MutableStateFlow("0.0")
    val averageFemale: StateFlow<String> = _averageFemale



    fun validateLogin(userId: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val patient = patientRepo.getPatientById(userId)
                Log.d("LoginViewModel", "Patient: $patient")
                onResult(patient?.password == password)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun registerPatient(
        userId: String,
        phoneNumber: String,
        name: String,
        password: String,
        onResult: (Boolean, String) -> Unit // pass error message too
    ) {
        viewModelScope.launch {
            // Validate phone number: only digits, exactly 10 characters
            if (!phoneNumber.matches(Regex("^\\d{10}$"))) {
                onResult(false, "Phone number must be exactly 10 digits.")
                return@launch
            }

            // Validate name: not empty or just spaces
            if (name.isBlank()) {
                onResult(false, "Name cannot be empty.")
                return@launch
            }

            // Validate password: at least 6 characters
            if (password.length < 6) {
                onResult(false, "Password must be at least 6 characters long.")
                return@launch
            }

            try {
                val patient = patientRepo.getPatientById(userId)
                if (patient != null) {
                    val updatedPatient = patient.copy(password = password, name = name, phoneNumber = phoneNumber)
                    patientRepo.updatePatient(updatedPatient)
                    onResult(true, "Patient registered successfully.")
                } else {
                    onResult(false, "Patient not found.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, "Registration failed due to an error.")
            }
        }
    }

    fun checkOptimalScore(userId: String) {
        viewModelScope.launch {
            val patient = patientRepo.getPatientById(userId)
            Log.d("PatientViewModel", "checkOptimalScore: patient = $patient")
            _isOptimalScore.value = patient != null &&
                    patient.fruitServeSize >= 2 &&
                    patient.fruitVariationScore >= 2
            Log.d("PatientViewModel", "checkOptimalScore: isOptimalScore = ${_isOptimalScore.value}")
        }
    }



    fun loadGenderScores(userId: String) {
        viewModelScope.launch {
            patientRepo.getGenderSpecificScores(userId)
        }
    }

    fun loadPatient(userId: String) {
        viewModelScope.launch {
            _patientLoaded.value = false
            val patient = patientRepo.getPatientById(userId)
            if (patient != null) {
                _name.value = patient.name.toString()
                _phoneNumber.value = patient.phoneNumber
                _userId.value = patient.userId
            }
            _patientLoaded.value = true
        }
    }

    fun loadInitialDataFromCSV(context: Context) {
        viewModelScope.launch {
            patientRepo.loadInitialPatientsFromCSVIfNeeded(context)
        }
    }

    fun loadHeifaAverages() {
        viewModelScope.launch(Dispatchers.IO) {
            val maleAvg = patientRepo.getAverageHeifaForMale()
            val femaleAvg = patientRepo.getAverageHeifaForFemale()

            withContext(Dispatchers.Main) {
                _averageMale.value = maleAvg.toString()
                _averageFemale.value = femaleAvg.toString()
            }
        }
    }


    fun findDataPatterns() {
        Log.d("PatientViewModel", "reach")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("PatientViewModel", "coroutine starts")

                // Fetch data from the repository
                val patients = patientRepo.getAllPatients()
                Log.d("PatientViewModel", "Patients: $patients")

                // Handle empty dataset case
                if (patients.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        dataPatterns = "No patient data available for analysis."
                    }
                    return@launch
                }

                val csvData = buildString {
                    appendLine("UserID, Gender, HEIFA_Total, Water, Wholegrain, Fruit, Vegetables")
                    patients.forEachIndexed { index, patient ->
                        val scores = patientRepo.getGenderSpecificScores(patient.userId)
                        appendLine(
                            "${index + 1}, ${patient.sex}, ${scores?.heifaTotalScore}, " +
                                    "${scores?.waterHeifaScore}, ${scores?.wholegrainsHeifaScore}, " +
                                    "${scores?.fruitHeifaScore}, ${scores?.vegetablesHeifaScore}"
                        )
                    }
                }
                Log.d("PatientViewModel", "csvData $csvData")

                val promptBase = """
                You are a data analyst AI reviewing a dataset of HEIFA nutrition scores and component intakes for various users.

                Your goal is to identify 3 meaningful and specific data patterns or insights from this dataset with each around 50 words.

                Dataset:
                $csvData

                Format the output like this:
                1. [Pattern Title]: [Short explanation with observed trend, range, or correlation].
                2. ...
                3. ...
            """.trimIndent()

                val response = generativeModel.generateContent(content { text(promptBase) })
                val result = response.text ?: "No patterns found."

                withContext(Dispatchers.Main) {
                    dataPatterns = result
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    dataPatterns = "Error: ${e.localizedMessage}"
                }
            }
        }
    }



    class LoginViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val context = context.applicationContext

        override fun <T: ViewModel> create(modelClass:Class<T>): T =
            PatientViewModel(context) as T
    }
}


data class Insight(val title: String, val content: String)

fun parseInsights(text: String): List<Insight> {
    return text.lines()
        .filter { it.matches(Regex("""^\d+\..*""")) }
        .mapNotNull {
            val splitIndex = it.indexOf(":")
            if (splitIndex != -1) {
                val title = it.substringBefore(":").trim()
                val content = it.substringAfter(":").trim()
                Insight(title = title, content = content)
            } else null
        }
}

fun stripMarkdown(text: String): String {
    return text.replace(Regex("""\*\*(.*?)\*\*"""), "$1")
        .replace(Regex("""\*(.*?)\*"""), "$1")
}