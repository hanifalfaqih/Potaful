package id.hanifalfaqih.potaful.ui.recommendation

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.databinding.ActivityRecommendationPlantLoadingBinding

class RecommendationPlantLoadingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecommendationPlantLoadingBinding

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

        binding.root.setOnClickListener {
            startActivity(Intent(this, RecommendationPlantResultActivity::class.java))
        }
    }
}