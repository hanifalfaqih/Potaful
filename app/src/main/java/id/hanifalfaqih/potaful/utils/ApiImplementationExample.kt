package id.hanifalfaqih.potaful.utils

/**
 * Complete Example Implementation
 * This file shows a complete example of how to use the API configuration
 * with PreferenceManager in an Activity
 */

/*
import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import kotlinx.coroutines.launch

class ExampleActivity : AppCompatActivity() {

    private lateinit var repository: ApiRepository
    private lateinit var preferenceManager: PreferenceManager
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repository and preference manager
        val apiService = ApiConfig.getApiService()
        repository = ApiRepository(apiService)
        preferenceManager = PreferenceManager(this)

        // Check if user is logged in
        if (preferenceManager.isLoggedIn()) {
            // User is logged in, load profile
            loadProfile()
        }
    }

    // Example: Login
    private fun performLogin(email: String, password: String) {
        showLoading("Logging in...")

        lifecycleScope.launch {
            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    hideLoading()

                    // Parse response and save token
                    // You need to create proper response models based on your API
                    // Example:
                    // val loginResponse = result.data as LoginResponse
                    // preferenceManager.saveAuthToken(loginResponse.token)
                    // preferenceManager.saveUserId(loginResponse.userId)
                    // preferenceManager.setLoggedIn(true)

                    Toast.makeText(this@ExampleActivity, "Login berhasil", Toast.LENGTH_SHORT).show()

                    // Navigate to next screen
                    // startActivity(Intent(this@ExampleActivity, DashboardActivity::class.java))
                    // finish()
                }
                is Result.Error -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    // Example: Register
    private fun performRegister(name: String, email: String, password: String) {
        showLoading("Creating account...")

        lifecycleScope.launch {
            when (val result = repository.register(name, email, password)) {
                is Result.Success -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()

                    // Auto login or navigate to login screen
                    performLogin(email, password)
                }
                is Result.Error -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    // Example: Get Profile
    private fun loadProfile() {
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val result = repository.getProfile(token)) {
                is Result.Success -> {
                    // Parse and display profile data
                    // val profileData = result.data as ProfileResponse
                    // updateUI(profileData)
                    Toast.makeText(this@ExampleActivity, "Profile loaded", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Show loading
                }
            }
        }
    }

    // Example: Get Plants List
    private fun loadPlants() {
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading("Loading plants...")

        lifecycleScope.launch {
            when (val result = repository.getPlants(token)) {
                is Result.Success -> {
                    hideLoading()
                    // Parse and display plants
                    // val plants = result.data as List<Plant>
                    // adapter.submitList(plants)
                    Toast.makeText(this@ExampleActivity, "Plants loaded", Toast.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    // Example: Add Plant
    private fun addNewPlant(plantName: String, plantType: String) {
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Create request body (you need to create proper data class)
        val plantData = mapOf(
            "name" to plantName,
            "type" to plantType,
            "wateringFrequency" to 7
        )

        showLoading("Adding plant...")

        lifecycleScope.launch {
            when (val result = repository.addPlant(token, plantData)) {
                is Result.Success -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Plant added successfully", Toast.LENGTH_SHORT).show()
                    loadPlants() // Reload the list
                }
                is Result.Error -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    // Example: Delete Plant
    private fun deletePlant(plantId: String) {
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading("Deleting plant...")

        lifecycleScope.launch {
            when (val result = repository.deletePlant(token, plantId)) {
                is Result.Success -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Plant deleted", Toast.LENGTH_SHORT).show()
                    loadPlants() // Reload the list
                }
                is Result.Error -> {
                    hideLoading()
                    Toast.makeText(this@ExampleActivity, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Already showing loading
                }
            }
        }
    }

    // Example: Logout
    private fun performLogout() {
        preferenceManager.clearUserData()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to login screen
        // startActivity(Intent(this, LoginActivity::class.java))
        // finish()
    }

    // Helper methods for loading dialog
    private fun showLoading(message: String) {
        progressDialog = ProgressDialog(this).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }
}
*/

