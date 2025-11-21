package id.hanifalfaqih.potaful.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import id.hanifalfaqih.potaful.R

class OnboardingUserActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var indicators: List<View>
    private val sharedViewModel: OnboardingSharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        setupViewPager()
        setupListeners()
    }

    private fun setupViews() {
        viewPager = findViewById(R.id.view_pager_onboarding)
        btnNext = findViewById(R.id.btn_next_onboarding)
        indicators = listOf(
            findViewById(R.id.indicator_1),
            findViewById(R.id.indicator_2),
            findViewById(R.id.indicator_3),
            findViewById(R.id.indicator_4)
        )
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        // Register callback untuk mengupdate indicator
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateButton(position)
            }
        })
    }

    private fun setupListeners() {
        btnNext.setOnClickListener {
            val currentItem = viewPager.currentItem
            if (currentItem < 3) {
                // Pindah ke page selanjutnya
                viewPager.currentItem = currentItem + 1
            } else {
                // Halaman terakhir, validasi dan navigasi ke loading activity
                if (validateInputs()) {
                    navigateToLoadingActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Please complete all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun navigateToLoadingActivity() {
        val intent = Intent(
            this,
            id.hanifalfaqih.potaful.ui.recommendation.RecommendationPlantLoadingActivity::class.java
        ).apply {
            putExtra("location", sharedViewModel.location.value)
            putExtra("skill_level", sharedViewModel.skillLevel.value)
            putExtra("home_frequency", sharedViewModel.homeFrequency.value)
            putExtra("preference", sharedViewModel.preference.value)
        }
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        val location = sharedViewModel.location.value
        val skillLevel = sharedViewModel.skillLevel.value
        val homeFrequency = sharedViewModel.homeFrequency.value
        val preference = sharedViewModel.preference.value

        return !location.isNullOrEmpty() &&
                !skillLevel.isNullOrEmpty() &&
                !homeFrequency.isNullOrEmpty() &&
                !preference.isNullOrEmpty()
    }


    private fun updateIndicators(position: Int) {
        indicators.forEachIndexed { index, indicator ->
            val colorRes = if (index == position) {
                R.color.forest_green_light
            } else {
                R.color.white
            }
            indicator.setBackgroundColor(ContextCompat.getColor(this, colorRes))
        }
    }

    private fun updateButton(position: Int) {
        if (position == 3) {
            btnNext.text = "Start"
        } else {
            btnNext.text = "Next"
        }
    }
}