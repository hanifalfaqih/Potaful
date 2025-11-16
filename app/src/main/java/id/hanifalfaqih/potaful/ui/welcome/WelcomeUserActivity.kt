package id.hanifalfaqih.potaful.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import id.hanifalfaqih.potaful.databinding.ActivityWelcomeUserBinding
import id.hanifalfaqih.potaful.ui.onboarding.OnboardingUserActivity

class WelcomeUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeUserBinding
    private val viewModel: WelcomeViewModel by viewModels {
        WelcomeViewModelFactory(ApiRepository(ApiConfig.getApiService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeUserBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnTanamSekarang.setOnClickListener {
            startActivity(Intent(this, OnboardingUserActivity::class.java))
        }

        binding.btnSignInWithGoogle.setOnClickListener {
            viewModel.fetchGoogleAuthUrl()
        }

        viewModel.authUrlState.observe(this) { state ->
            when (state) {
                is WelcomeViewModel.AuthUrlState.Loading -> {
                    // Optional: show progress on button
                }

                is WelcomeViewModel.AuthUrlState.Success -> {
                    val intent = Intent(Intent.ACTION_VIEW, state.url.toUri())
                    startActivity(intent)
                }

                is WelcomeViewModel.AuthUrlState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}