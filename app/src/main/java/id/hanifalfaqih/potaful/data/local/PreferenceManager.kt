package id.hanifalfaqih.potaful.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "potaful_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHOTO = "user_photo"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_IS_ONBOARDING_COMPLETED = "is_onboarding_completed"
    }

    // Authentication Token
    fun saveAuthToken(token: String) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return preferences.getString(KEY_AUTH_TOKEN, null)
    }

    // User ID
    fun saveUserId(userId: String) {
        preferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return preferences.getString(KEY_USER_ID, null)
    }

    // User Name
    fun saveUserName(name: String) {
        preferences.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String? {
        return preferences.getString(KEY_USER_NAME, null)
    }

    // User Email
    fun saveUserEmail(email: String) {
        preferences.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    fun getUserEmail(): String? {
        return preferences.getString(KEY_USER_EMAIL, null)
    }

    // User Photo URL
    fun saveUserPhoto(photoUrl: String) {
        preferences.edit().putString(KEY_USER_PHOTO, photoUrl).apply()
    }

    fun getUserPhoto(): String? {
        return preferences.getString(KEY_USER_PHOTO, null)
    }

    // Login Status
    fun setLoggedIn(isLoggedIn: Boolean) {
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Onboarding Status
    fun setOnboardingCompleted(isCompleted: Boolean) {
        preferences.edit().putBoolean(KEY_IS_ONBOARDING_COMPLETED, isCompleted).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean(KEY_IS_ONBOARDING_COMPLETED, false)
    }

    // Clear all user data (for logout)
    fun clearUserData() {
        preferences.edit().apply {
            remove(KEY_AUTH_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            remove(KEY_USER_PHOTO)
            putBoolean(KEY_IS_LOGGED_IN, false)
        }.apply()
    }

    // Clear all preferences
    fun clearAll() {
        preferences.edit().clear().apply()
    }
}

