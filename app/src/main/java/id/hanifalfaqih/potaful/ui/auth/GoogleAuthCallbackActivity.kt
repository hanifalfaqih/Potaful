package id.hanifalfaqih.potaful.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import id.hanifalfaqih.potaful.ui.dashboard.DashboardActivity
import id.hanifalfaqih.potaful.ui.welcome.WelcomeUserActivity
import kotlinx.coroutines.launch

class GoogleAuthCallbackActivity : AppCompatActivity() {

    private lateinit var pref: PreferenceManager
    private lateinit var repository: ApiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = PreferenceManager(this)
        repository = ApiRepository(ApiConfig.getApiService())

        // Handle deep link
        val data: Uri? = intent?.data
        val dataString = intent?.dataString
        Log.d("GoogleCallback", "Incoming URI: $data , dataString=$dataString")

        // Check if backend returned error status via query
        val status = data?.getQueryParameter("status")
        val message = data?.getQueryParameter("message")
        if (status.equals("FAILED", ignoreCase = true)) {
            val msg = message?.replace('+', ' ')?.let { Uri.decode(it) } ?: "Login gagal."
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            navigateToWelcome()
            return
        }

        // Try common parameter keys
        var token: String? = data?.getQueryParameter("token")
        if (token.isNullOrEmpty()) token = data?.getQueryParameter("access_token")
        if (token.isNullOrEmpty()) token = data?.getQueryParameter("auth_token")

        // Some providers return token in fragment (#token=...)
        if (token.isNullOrEmpty()) {
            val fragment = data?.fragment
            if (!fragment.isNullOrEmpty()) {
                val params = fragment.split("&").mapNotNull {
                    val parts = it.split("=")
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()
                token = params["token"] ?: params["access_token"] ?: params["auth_token"]
            }
        }

        Log.d("GoogleCallback", "Extracted token: ${token?.take(10)}... (null means not found)")

        if (!token.isNullOrEmpty()) {
            // Save token and set logged in status
            pref.saveAuthToken(token)
            pref.setLoggedIn(true)
            pref.setOnboardingCompleted(true)

            // Fetch user profile
            fetchUserProfile(token)
        } else {
            Toast.makeText(this, "Login gagal. Token tidak ditemukan.", Toast.LENGTH_LONG).show()
            navigateToWelcome()
        }
    }

    private fun fetchUserProfile(token: String) {
        lifecycleScope.launch {
            when (val result = repository.getUserProfile(token)) {
                is Result.Success -> {
                    val userData = result.data.data.user

                    // Combine first_name and last_name
                    val fullName = "${userData.firstName} ${userData.lastName}".trim()

                    // Save user data to preferences
                    pref.saveUserId(userData.id)
                    pref.saveUserName(fullName)
                    pref.saveUserEmail(userData.email)
                    userData.photo?.let { pref.saveUserPhoto(it) }

                    Toast.makeText(
                        this@GoogleAuthCallbackActivity,
                        "Login berhasil! 🎉",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to Dashboard
                    navigateToDashboard()
                }

                is Result.Error -> {
                    // Even if profile fetch fails, still navigate to dashboard
                    Log.d("GoogleCallback", "Profile fetch failed: ${result.message}")
                    Toast.makeText(
                        this@GoogleAuthCallbackActivity,
                        "Login berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToDashboard()
                }

                is Result.Loading -> {
                    // Handled by when
                }
            }
        }
    }

    private fun navigateToWelcome() {
        val intent = Intent(this, WelcomeUserActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
