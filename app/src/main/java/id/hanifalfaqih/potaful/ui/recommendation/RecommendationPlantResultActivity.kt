package id.hanifalfaqih.potaful.ui.recommendation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.remote.response.PlantRecommendationResponse
import id.hanifalfaqih.potaful.databinding.ActivityRecommendationPlantResultBinding
import id.hanifalfaqih.potaful.ui.dashboard.DashboardActivity

class RecommendationPlantResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationPlantResultBinding
    private lateinit var adapter: PlantRecommendationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendationPlantResultBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        parseAndDisplayRecommendations()

        binding.btnGoToDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvPlantRecommendations.layoutManager = LinearLayoutManager(this)
    }

    private fun parseAndDisplayRecommendations() {
        val jsonString = intent.getStringExtra("recommendation_data")

        if (jsonString.isNullOrEmpty()) {
            Toast.makeText(this, "No recommendation data found", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val gson = Gson()
            val type = object : TypeToken<List<PlantRecommendationResponse>>() {}.type
            val response: List<PlantRecommendationResponse> = gson.fromJson(jsonString, type)

            // Extract plant names and reasons
            val plantItems = mutableListOf<PlantRecommendationItem>()

            if (response.isNotEmpty() && response[0].output.isNotEmpty()) {
                val output = response[0].output[0]
                if (output.content.isNotEmpty()) {
                    val textData = output.content[0].text
                    val recommendations = textData.recommendation
                    val reasons = textData.reason

                    // Combine recommendations with reasons
                    for (i in recommendations.indices) {
                        plantItems.add(
                            PlantRecommendationItem(
                                name = recommendations[i],
                                reason = if (i < reasons.size) reasons[i] else "No reason provided"
                            )
                        )
                    }
                }
            }

            // Set adapter
            adapter = PlantRecommendationAdapter(plantItems)
            binding.rvPlantRecommendations.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error parsing recommendations: ${e.message}", Toast.LENGTH_LONG)
                .show()
        }
    }
}