package id.hanifalfaqih.potaful.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.databinding.ActivityProfileBinding
import id.hanifalfaqih.potaful.ui.welcome.WelcomeUserActivity

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)

        loadUserProfile()
        setupClickListeners()
    }

    private fun loadUserProfile() {
        // Load user data from preferences
        binding.etName.setText(preferenceManager.getUserName() ?: "")
        binding.etLocation.setText("") // Add location to PreferenceManager if needed

        // Load user photo
        val photoUrl = preferenceManager.getUserPhoto()
        if (!photoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.bg_welcome_screen)
                .error(R.drawable.bg_welcome_screen)
                .into(binding.ivProfileUser)
        } else {
            // Set default image if no photo
            binding.ivProfileUser.setImageResource(R.drawable.bg_welcome_screen)
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnChangePhoto.setOnClickListener {
            // TODO: Implement image picker
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString()
        // val location = binding.etLocation.text.toString() // TODO: Add location field to PreferenceManager

        if (name.isNotEmpty()) {
            preferenceManager.saveUserName(name)
            Toast.makeText(this, "Profile berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        // Clear all user data
        preferenceManager.clearUserData()

        Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()

        // Navigate to welcome screen and clear back stack
        val intent = Intent(this, WelcomeUserActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}