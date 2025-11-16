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
        setupWeather()
    }

    private fun setupRecyclerViews() {
        potSummaryAdapter = PotSummaryAdapter(onWaterClick = { pot ->
            Toast.makeText(this, "Siram: ${pot.typeName}", Toast.LENGTH_SHORT).show()
        }, onItemClick = { pot ->
            // Scroll to corresponding item in main list and expand
            val position = potDashboardAdapter.items.indexOfFirst { it.potId == pot.potId }
            if (position != -1) {
                binding.rvPotList.smoothScrollToPosition(position)
                if (!potDashboardAdapter.isExpanded(pot.potId)) {
                    val token = preferenceManager.getAuthToken().orEmpty()
                    viewModel.loadPotDetail(token, pot.potId)
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
                if (potDashboardAdapter.isExpanded(pot.potId)) {
                    // Collapse
                    potDashboardAdapter.updateExpandedItem(pot.potId, null)
                } else {
                    // Expand - fetch detail data
                    val token = preferenceManager.getAuthToken().orEmpty()
                    viewModel.loadPotDetail(token, pot.potId)
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
            refreshWeather()
        }
        swipeRefresh.setColorSchemeResources(
            R.color.forest_brown_dark,
            R.color.forest_brown_darkest,
            R.color.text_green_dark
        )
    }

    private fun refreshWeather() {
        val city = binding.tvLocationUser.text?.toString()!!.ifEmpty { "Tangerang" }
        viewModel.loadWeather(city, BuildConfig.OPEN_WEATHER_API_KEY)
    }

    private fun setupListeners() {
        binding.ivProfileUser.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
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
                    potSummaryAdapter.submitList(state.pots)
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
                    showLoadingOverlay("Menambahkan pot...")
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
                    // Optionally show small shimmer or placeholder
                    binding.tvCloudConditionUser.text = "Memuat cuaca..."
                }

                is DashboardViewModel.WeatherState.Success -> {
                    bindWeather(state.data)
                }

                is DashboardViewModel.WeatherState.Error -> {
                    binding.tvCloudConditionUser.text = "Gagal memuat cuaca"
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
                        "Gagal memuat detail: ${state.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    potDashboardAdapter.updateExpandedItem(state.potId, null)
                }
            }
        }
    }

    private fun bindWeather(data: WeatherResponse) {
        val condition =
            data.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "-"
        binding.tvCloudConditionUser.text = condition
        binding.tvAirHumidity.text = "Kelembapan udara: ${data.main.humidity}%"
        binding.tvTemperatureUser.text =
            String.format(Locale.getDefault(), "%.0f°C", data.main.temp)
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
            .setTitle("Tambah Pot")
            .setMessage("Masukkan ID pot yang ingin ditambahkan")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val potId = etPotId.text.toString().trim()
                val token = preferenceManager.getAuthToken().orEmpty()
                viewModel.addPot(token, potId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun loadMyPots() {
        val token = preferenceManager.getAuthToken().orEmpty()
        viewModel.loadMyPots(token)
    }

    private fun setupWeather() {
        // Set initial date/time
        val locale = Locale("in", "ID")
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", locale)
        val timeFormat = SimpleDateFormat("HH:mm", locale)
        binding.tvDateUser.text = dateFormat.format(Date())
        binding.tvTimeUser.text = timeFormat.format(Date())

        // Load weather for a default city (could be user preference later)
        val city = "Tangerang"
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
        // Load user name
        val userName = preferenceManager.getUserName() ?: "Pengguna"
        binding.tvGreetingUser.text = "Halo, $userName!"

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

    override fun onDestroy() {
        super.onDestroy()
    }
}