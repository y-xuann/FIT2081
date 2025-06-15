package com.fit2081.yangxuan_33520496

import android.content.Context
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.yangxuan_33520496.data.NutriCoachTips
import com.fit2081.yangxuan_33520496.data.NutriCoachTipsDao
import com.fit2081.yangxuan_33520496.data.NutriCoachTipsRepository
import com.fit2081.yangxuan_33520496.data.PatientRepository
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GenAIViewModel(
    private val motivationalTipDao: NutriCoachTipsDao,
    private val clinicianId: String,
) : ViewModel() {
    private val repository = NutriCoachTipsRepository(motivationalTipDao)

    var tips = mutableStateListOf<NutriCoachTips>()
        private set

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    // Generative AI model
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    init {
        loadTips()
    }

    fun loadTips() {
        viewModelScope.launch {
            val fetched = repository.getTipsByClinician(clinicianId)
            tips.clear()
            tips.addAll(fetched)
        }
    }

    fun sendPrompt(
        promptBase: String,
        fruitDetails: Map<String, String>,
        isOptimalScore: Boolean
    ) {
        uiState = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val contextPrompt = buildString {
                    append(promptBase)
                    append("\n\nPatient's recent fruit intake:\n")
                    fruitDetails.forEach { (key, value) ->
                        append("- $key: $value\n")
                    }

                    append(
                        "\nHealth analysis: The patient's current nutritional score is ${
                            if (isOptimalScore) "optimal" else "not optimal"
                        }.\n"
                    )

                    append("Generate a short, encouraging message tailored to this situation.")
                }

                val response = generativeModel.generateContent(content { text(contextPrompt) })
                val outputText = response.text ?: return@launch

                val tip = NutriCoachTips(clinicianId = clinicianId, message = outputText)
                motivationalTipDao.insertTip(tip)

                withContext(Dispatchers.Main) {
                    tips.add(tip)
                    uiState = UiState.Success(outputText)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState = UiState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun deleteTip(tip: NutriCoachTips) {
        viewModelScope.launch {
            motivationalTipDao.deleteTip(tip)
            loadTips() // refresh list
        }
    }




    // Factory for ViewModel injection
    class GenAIViewModelFactory(
        private val dao: NutriCoachTipsDao,
        private val clinicianId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GenAIViewModel::class.java)) {
                return GenAIViewModel(dao, clinicianId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
