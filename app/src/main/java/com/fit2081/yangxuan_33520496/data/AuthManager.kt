package com.fit2081.yangxuan_33520496.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object AuthManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_FIRST_LOGIN = "first_login"

    private lateinit var preferences: SharedPreferences

    internal val _userId: MutableState<String?> = mutableStateOf(null)
    val userId: State<String?> get() = _userId

    // Call this once at app startup, e.g. in MainActivity.onCreate()
    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val storedUserId = preferences.getString(KEY_USER_ID, null)
        if (storedUserId != null) {
            _userId.value = storedUserId
        }
    }

    fun login(userId: String) {
        _userId.value = userId
        Log.d("AuthManager", "Logged in as $userId")
        preferences.edit()
            .putString(KEY_USER_ID, userId)
            .putBoolean("first_login", true) // Mark as first time
            .apply()

    }

    fun logout() {
        _userId.value = null
        preferences.edit()
            .remove(KEY_USER_ID)
            .remove(KEY_FIRST_LOGIN)
            .apply()
    }

    fun getPatientId(): String? {
        return _userId.value
    }

    fun isFirstLogin(): Boolean = preferences.getBoolean(KEY_FIRST_LOGIN, false)

    fun setFirstLoginDone() {
        preferences.edit().putBoolean(KEY_FIRST_LOGIN, false).apply()
    }

    fun setFirstLoginNotDone() {
        preferences.edit().putBoolean(KEY_FIRST_LOGIN, true).apply()
    }


}
