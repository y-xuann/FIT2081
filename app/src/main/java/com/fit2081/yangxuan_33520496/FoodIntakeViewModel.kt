package com.fit2081.yangxuan_33520496

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.yangxuan_33520496.data.FoodIntake
import com.fit2081.yangxuan_33520496.data.FoodIntakeRepository
import kotlinx.coroutines.launch

class FoodIntakeViewModel(context: Context) : ViewModel() {
    private val repository = FoodIntakeRepository(context)

    // Mutable states
    private val _selectedFoods = mutableStateOf(setOf<String>())
    val selectedFoods: State<Set<String>> = _selectedFoods

    private val _selectedPersona = mutableStateOf("")
    val selectedPersona: State<String> = _selectedPersona

    private val _biggestMealTime = mutableStateOf("")
    val biggestMealTime: State<String> = _biggestMealTime

    private val _sleepTime = mutableStateOf("")
    val sleepTime: State<String> = _sleepTime

    private val _wakeUpTime = mutableStateOf("")
    val wakeUpTime: State<String> = _wakeUpTime

    fun selectPersona(persona: String) {
        _selectedPersona.value = persona
    }

    fun toggleFoodCategory(clinicianId: String, food: String) {
        viewModelScope.launch {
            val existing = repository.getByUser(clinicianId)
            Log.d("UpdatePersona", "Existing food intake: $existing")
            if (existing != null) {
                // Update the selectedFoods set
                val updatedSet = _selectedFoods.value.toMutableSet()
                if (updatedSet.contains(food)) {
                    updatedSet.remove(food)
                } else {
                    updatedSet.add(food)
                }

                // Create updated FoodIntake with new foodCategories
                val updatedIntake = existing.copy(foodCategories = updatedSet.joinToString(","))

                // Save to DB
                repository.updateFoodIntake(updatedIntake)

                // Update UI state
                _selectedFoods.value = updatedSet
            } else {
                Log.e("UpdatePersona", "No existing food intake found for userId: $clinicianId")
            }
        }
    }


    fun updatePersona(clinicianId: String, newPersona: String) {
        viewModelScope.launch {
            val existing = repository.getByUser(clinicianId)
            Log.d("UpdatePersona", "Existing food intake: $existing")
            if (existing != null) {
                val updated = existing.copy(persona = newPersona)
                repository.updateFoodIntake(updated)
                _selectedPersona.value = newPersona
            } else {
                Log.e("UpdatePersona", "No existing food intake found for userId: $clinicianId")
            }
        }
    }


    fun updateBiggestMealTime(clinicianId:String, time: String) {
        viewModelScope.launch {
            val existing = repository.getByUser(clinicianId)
            Log.d("UpdatePersona", "Existing food intake: $existing")
            if (existing != null) {
                val updated = existing.copy(biggestMealTime = time)
                repository.updateFoodIntake(updated)
                _biggestMealTime.value = time
            } else {
                Log.e("UpdatePersona", "No existing food intake found for userId: $clinicianId")
            }
        }
    }

    fun updateSleepTime(clinicianId:String, time: String) {
        viewModelScope.launch {
            val existing = repository.getByUser(clinicianId)
            Log.d("UpdatePersona", "Existing food intake: $existing")
            if (existing != null) {
                val updated = existing.copy(sleepTime = time)
                repository.updateFoodIntake(updated)
                _sleepTime.value = time
            } else {
                Log.e("UpdatePersona", "No existing food intake found for userId: $clinicianId")
            }
        }
    }

    fun updateWakeUpTime(clinicianId:String, time: String) {
        viewModelScope.launch {
            val existing = repository.getByUser(clinicianId)
            Log.d("UpdatePersona", "Existing food intake: $existing")
            if (existing != null) {
                val updated = existing.copy(wakeUpTime = time)
                repository.updateFoodIntake(updated)
                _wakeUpTime.value = time
            } else {
                Log.e("UpdatePersona", "No existing food intake found for userId: $clinicianId")
            }
        }
    }



    fun saveFoodIntake(userId: String) {
        viewModelScope.launch {
            val foodIntake = FoodIntake(
                userId = userId,
                persona = _selectedPersona.value,
                foodCategories = _selectedFoods.value.joinToString(","),
                biggestMealTime = _biggestMealTime.value,
                sleepTime = _sleepTime.value,
                wakeUpTime = _wakeUpTime.value
            )
            repository.insert(foodIntake)
        }
    }

    fun loadFoodIntakeByUser(userId: String) {
        viewModelScope.launch {
            val intake = repository.getByUser(userId)
            if (intake != null) {
                _selectedPersona.value = intake.persona
                _selectedFoods.value = intake.foodCategories.split(",").toSet()
                _biggestMealTime.value = intake.biggestMealTime
                _sleepTime.value = intake.sleepTime
                _wakeUpTime.value = intake.wakeUpTime
            } else {
                // Create new intake
                val newIntake = FoodIntake(
                    userId = userId,
                    persona = "",
                    foodCategories = "",
                    biggestMealTime = "",
                    sleepTime = "",
                    wakeUpTime = ""
                )
                repository.insert(newIntake)
        }}
    }

    class FoodIntakeViewModelFactory(context: Context) : ViewModelProvider.Factory {
        private val appContext = context.applicationContext
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(appContext) as T
        }
    }
}
