package id.hanifalfaqih.potaful.ui.recommendation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.databinding.ActivityRecommendationPlantLoadingBinding
import kotlinx.coroutines.launch

class RecommendationPlantLoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationPlantLoadingBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationPlantLoadingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)

        // Get data from intent
        val location = intent.getStringExtra("location") ?: ""
        val skillLevel = intent.getStringExtra("skill_level") ?: ""
        val homeFrequency = intent.getStringExtra("home_frequency") ?: ""
        val preference = intent.getStringExtra("preference") ?: ""

        // Call API
        getPlantRecommendation(location, skillLevel, homeFrequency, preference)
    }

    private fun getPlantRecommendation(
        location: String,
        skillLevel: String,
        homeFrequency: String,
        preference: String
    ) {
        lifecycleScope.launch {
            try {
                val response = ApiConfig.getN8NApiService().getPlantRecommendation(
                    location = location,
                    skillLevel = skillLevel,
                    homeFrequency = homeFrequency,
                    preference = preference
                )

                if (response.isNotEmpty()) {
                    // Save to preferences
                    val gson = Gson()
                    val jsonString = gson.toJson(response)
                    preferenceManager.savePlantRecommendation(jsonString)

                    // Mark onboarding completed
                    preferenceManager.setOnboardingCompleted(true)

                    // Navigate to result activity with data
                    val intent = Intent(
                        this@RecommendationPlantLoadingActivity,
                        RecommendationPlantResultActivity::class.java
                    ).apply {
                        putExtra("recommendation_data", jsonString)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@RecommendationPlantLoadingActivity,
                        "No recommendations found",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RecommendationPlantLoadingActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }
}