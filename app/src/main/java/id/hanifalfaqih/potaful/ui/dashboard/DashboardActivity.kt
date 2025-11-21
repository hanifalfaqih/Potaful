package id.hanifalfaqih.potaful.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.hanifalfaqih.potaful.BuildConfig
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.remote.response.WeatherResponse
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import id.hanifalfaqih.potaful.databinding.ActivityDashboardBinding
import id.hanifalfaqih.potaful.ui.dashboard.adapter.PotDashboardAdapter
import id.hanifalfaqih.potaful.ui.dashboard.adapter.PotSummaryAdapter
import id.hanifalfaqih.potaful.ui.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(ApiRepository(ApiConfig.getApiService()))
    }

    private lateinit var potSummaryAdapter: PotSummaryAdapter
    private lateinit var potDashboardAdapter: PotDashboardAdapter

    companion object {
        private const val REQUEST_PROFILE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)
        swipeRefresh = findViewById(R.id.swipe_refresh)

        setupRecyclerViews()
        setupListeners()
        setupObservers()
        loadUserProfile()
        loadMyPots()
        loadHydration() // load summary list from hydration endpoint
        setupWeather()
    }

    private fun setupRecyclerViews() {
        potSummaryAdapter = PotSummaryAdapter(onWaterClick = { pot ->
            val token = preferenceManager.getAuthToken().orEmpty()
            pot.id?.let { potId ->
                viewModel.wateringPot(token, potId)
            }
        }, onItemClick = { pot ->
            // Scroll to corresponding item in main list and expand
            pot.id?.let { potId ->
                val position = potDashboardAdapter.items.indexOfFirst { it.potId == potId }
                if (position != -1) {
                    binding.rvPotList.smoothScrollToPosition(position)
                    if (!potDashboardAdapter.isExpanded(potId)) {
                        val token = preferenceManager.getAuthToken().orEmpty()
                        viewModel.loadPotDetail(token, potId)
                    }
                }
            }
        })
        binding.rvPotSummary.apply {
            layoutManager =
                LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = potSummaryAdapter
        }

        potDashboardAdapter = PotDashboardAdapter(
            onItemClick = { pot ->
                // Toggle expand/collapse
                pot.potId?.let { potId ->
                    if (potDashboardAdapter.isExpanded(potId)) {
                        // Collapse
                        potDashboardAdapter.updateExpandedItem(potId, null)
                    } else {
                        // Expand - fetch detail data
                        val token = preferenceManager.getAuthToken().orEmpty()
                        viewModel.loadPotDetail(token, potId)
                    }
                }
            },
            onAddPotClick = {
                showAddPotDialog()
            }
        )
        binding.rvPotList.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = potDashboardAdapter
        }

        // Setup SwipeRefreshLayout
        swipeRefresh.setOnRefreshListener {
            loadMyPots()
            loadHydration()
            refreshWeather()
        }
        swipeRefresh.setColorSchemeResources(
            R.color.forest_brown_dark,
            R.color.forest_brown_darkest,
            R.color.text_green_dark
        )
    }

    private fun refreshWeather() {
        val savedLocation = preferenceManager.getUserLocation()
        val city = savedLocation?.ifBlank { "Tangerang" } ?: "Tangerang"
        viewModel.loadWeather(city, BuildConfig.OPEN_WEATHER_API_KEY)
    }

    private fun setupListeners() {
        binding.ivProfileUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivityForResult(intent, REQUEST_PROFILE)
        }
    }

    private fun setupObservers() {
        // Observe pots state
        viewModel.potsState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.PotsState.Loading -> {
                    // Only show loading overlay if not refreshing
                    if (!swipeRefresh.isRefreshing) {
                        showLoadingOverlay("Memuat data pot...")
                    }
                }

                is DashboardViewModel.PotsState.Success -> {
                    hideLoadingOverlay()
                    swipeRefresh.isRefreshing = false
                    potDashboardAdapter.submitList(state.pots)
                }

                is DashboardViewModel.PotsState.Error -> {
                    hideLoadingOverlay()
                    swipeRefresh.isRefreshing = false
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    Log.d("DashboardActivity", "Error loading pots: ${state.message}")
                }
            }
        }

        // Observe add pot state
        viewModel.addPotState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.AddPotState.Idle -> {
                    // Do nothing
                }

                is DashboardViewModel.AddPotState.Loading -> {
                    showLoadingOverlay("Loading pot data...")
                }

                is DashboardViewModel.AddPotState.Success -> {
                    hideLoadingOverlay()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetAddPotState()
                    loadMyPots() // Reload pots list
                }

                is DashboardViewModel.AddPotState.Error -> {
                    hideLoadingOverlay()
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetAddPotState()
                }
            }
        }

        viewModel.weatherState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.WeatherState.Loading -> {
                    binding.tvCloudConditionUser.text = "Loading weather..." // fallback literal
                }

                is DashboardViewModel.WeatherState.Success -> bindWeather(state.data)

                is DashboardViewModel.WeatherState.Error -> {
                    binding.tvCloudConditionUser.text = "Failed to load weather" // fallback literal
                }
            }
        }

        // Observe pot detail for expand/collapse
        viewModel.potDetailState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.PotDetailState.Loading -> {
                    // Optionally show loading on specific item
                }

                is DashboardViewModel.PotDetailState.Success -> {
                    potDashboardAdapter.updateExpandedItem(state.potId, state.data)
                }

                is DashboardViewModel.PotDetailState.Error -> {
                    Toast.makeText(
                        this,
                        "Failed to load detail: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    potDashboardAdapter.updateExpandedItem(state.potId, null)
                }
            }
        }

        viewModel.wateringState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.WateringState.Idle -> {
                    // Do nothing
                }

                is DashboardViewModel.WateringState.Loading -> {
                    Toast.makeText(this, "Sending watering command...", Toast.LENGTH_SHORT).show()
                }

                is DashboardViewModel.WateringState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // After success, refresh detail if the item is currently expanded
                    val potId = state.potId
                    if (potDashboardAdapter.isExpanded(potId)) {
                        val token = preferenceManager.getAuthToken().orEmpty()
                        viewModel.loadPotDetail(token, potId)
                    }
                    viewModel.resetWateringState()
                }

                is DashboardViewModel.WateringState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetWateringState()
                }
            }
        }

        // Observe hydration summary for rv_summary
        viewModel.hydrationState.observe(this) { state ->
            when (state) {
                is DashboardViewModel.HydrationState.Loading -> {
                    // optional: show placeholder on rv_summary
                }

                is DashboardViewModel.HydrationState.Success -> {
                    potSummaryAdapter.submitList(state.pots)
                }

                is DashboardViewModel.HydrationState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindWeather(data: WeatherResponse) {
        val condition =
            data.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "-"
        binding.tvCloudConditionUser.text = condition
        binding.tvAirHumidity.text = "Air humidity: ${data.main.humidity}%" // fallback literal
        binding.tvTemperatureUser.text =
            String.format(Locale.getDefault(), "%.0f°C", data.main.temp)

        // Display city name from weather API (based on saved location from local DB)
        binding.tvLocationUser.text = data.name

        val iconCode = data.weather.firstOrNull()?.icon ?: ""
        binding.ivCloudConditionUser.setImageResource(mapWeatherIcon(iconCode))
    }

    private fun mapWeatherIcon(code: String): Int = when (code) {
        "01d" -> R.drawable.ic_weather_clear_day
        "01n" -> R.drawable.ic_weather_clear_night
        "02d" -> R.drawable.ic_weather_partly_cloudy_day
        "02n" -> R.drawable.ic_weather_partly_cloudy_night
        "03d", "03n", "04d", "04n" -> R.drawable.ic_weather_cloudy
        "09d", "09n" -> R.drawable.ic_weather_drizzle
        "10d", "10n" -> R.drawable.ic_weather_rain
        "11d", "11n" -> R.drawable.ic_weather_thunder
        "13d", "13n" -> R.drawable.ic_weather_snow
        "50d", "50n" -> R.drawable.ic_weather_mist
        else -> R.drawable.ic_weather_unknown
    }

    private fun showAddPotDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_pot, null)
        val etPotId = dialogView.findViewById<EditText>(R.id.et_pot_id)

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Pot")
            .setMessage("Enter the pot ID you want to add")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val potId = etPotId.text.toString().trim()
                val token = preferenceManager.getAuthToken().orEmpty()
                viewModel.addPot(token, potId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadMyPots() {
        val token = preferenceManager.getAuthToken().orEmpty()
        viewModel.loadMyPots(token)
    }

    private fun loadHydration() {
        val token = preferenceManager.getAuthToken().orEmpty()
        viewModel.loadHydrationSummary(token)
    }

    private fun setupWeather() {
        val locale = Locale.forLanguageTag("en-US")
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        binding.tvDateUser.text = dateFormat.format(Date())
        binding.tvTimeUser.text = timeFormat.format(Date())

        // Load location from local database (PreferenceManager)
        val savedLocation = preferenceManager.getUserLocation()
        val city = savedLocation?.ifBlank { "Tangerang" } ?: "Tangerang"
        Log.d("DashboardActivity", "Loading weather for location from local DB: $city")
        viewModel.loadWeather(city, BuildConfig.OPEN_WEATHER_API_KEY)
    }

    private fun showLoadingOverlay(message: String) {
        binding.loadingOverlay.visibility = View.VISIBLE
        binding.tvLoadingMessage.text = message
    }

    private fun hideLoadingOverlay() {
        binding.loadingOverlay.visibility = View.GONE
    }

    private fun loadUserProfile() {
        val userName = preferenceManager.getUserName() ?: "User" // fallback literal
        binding.tvGreetingUser.text = String.format(Locale.getDefault(), "Halo, %s!", userName)

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

    override fun onResume() {
        super.onResume()
        // Refresh profile data when returning to dashboard
        // This ensures any profile changes are reflected
        loadUserProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PROFILE && resultCode == RESULT_OK) {
            // Profile was updated, refresh the UI
            loadUserProfile()

            // Also refresh weather if location was changed
            refreshWeather()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}