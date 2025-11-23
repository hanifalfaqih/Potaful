package id.hanifalfaqih.potaful.ui.allpots

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.remote.response.HydrationPotItem
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import id.hanifalfaqih.potaful.databinding.ActivityAllPotsBinding
import id.hanifalfaqih.potaful.ui.allpots.adapter.AllPotsAdapter

class AllPotsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllPotsBinding
    private lateinit var preferenceManager: PreferenceManager

    private val viewModel: AllPotsViewModel by viewModels {
        AllPotsViewModelFactory(ApiRepository(ApiConfig.getApiService()))
    }

    private lateinit var urgentAdapter: AllPotsAdapter
    private lateinit var warningAdapter: AllPotsAdapter
    private lateinit var safeAdapter: AllPotsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllPotsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loading_overlay)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        preferenceManager = PreferenceManager(this)

        setupRecyclerViews()
        setupListeners()
        setupObservers()
        loadData()
    }

    private fun setupRecyclerViews() {
        // Urgent RecyclerView
        urgentAdapter = AllPotsAdapter(onWaterClick = { pot ->
            val token = preferenceManager.getAuthToken().orEmpty()
            pot.id?.let { potId ->
                viewModel.wateringPot(token, potId)
            }
        })
        binding.rvUrgent.apply {
            layoutManager = LinearLayoutManager(this@AllPotsActivity)
            adapter = urgentAdapter
        }

        // Warning RecyclerView
        warningAdapter = AllPotsAdapter(onWaterClick = { pot ->
            val token = preferenceManager.getAuthToken().orEmpty()
            pot.id?.let { potId ->
                viewModel.wateringPot(token, potId)
            }
        })
        binding.rvWarning.apply {
            layoutManager = LinearLayoutManager(this@AllPotsActivity)
            adapter = warningAdapter
        }

        // Safe RecyclerView
        safeAdapter = AllPotsAdapter(onWaterClick = { pot ->
            val token = preferenceManager.getAuthToken().orEmpty()
            pot.id?.let { potId ->
                viewModel.wateringPot(token, potId)
            }
        })
        binding.rvSafe.apply {
            layoutManager = LinearLayoutManager(this@AllPotsActivity)
            adapter = safeAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.swipeRefresh.setOnRefreshListener {
            loadData()
        }

        binding.swipeRefresh.setColorSchemeResources(
            R.color.forest_brown_dark,
            R.color.forest_brown_darkest,
            R.color.text_green_dark
        )
    }

    private fun setupObservers() {
        viewModel.hydrationState.observe(this) { state ->
            when (state) {
                is AllPotsViewModel.HydrationState.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        showLoadingOverlay()
                    }
                }

                is AllPotsViewModel.HydrationState.Success -> {
                    hideLoadingOverlay()
                    binding.swipeRefresh.isRefreshing = false
                    displayGroupedPots(state.pots)
                }

                is AllPotsViewModel.HydrationState.Error -> {
                    hideLoadingOverlay()
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.wateringState.observe(this) { state ->
            when (state) {
                is AllPotsViewModel.WateringState.Idle -> {
                    // Do nothing
                }

                is AllPotsViewModel.WateringState.Loading -> {
                    Toast.makeText(this, "Sending watering command...", Toast.LENGTH_SHORT).show()
                }

                is AllPotsViewModel.WateringState.Success -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    // Refresh the list after successful watering
                    loadData()
                    viewModel.resetWateringState()
                }

                is AllPotsViewModel.WateringState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetWateringState()
                }
            }
        }
    }

    private fun displayGroupedPots(pots: List<HydrationPotItem>) {
        // Group pots by condition
        val urgentPots = pots.filter { it.condition?.uppercase() == "URGENT" }
        val warningPots = pots.filter { it.condition?.uppercase() == "WARNING" }
        val safePots = pots.filter { it.condition?.uppercase() == "SAFE" }

        // Show/hide sections based on availability
        if (urgentPots.isNotEmpty()) {
            binding.tvUrgentTitle.visibility = View.VISIBLE
            binding.rvUrgent.visibility = View.VISIBLE
            urgentAdapter.submitList(urgentPots)
        } else {
            binding.tvUrgentTitle.visibility = View.GONE
            binding.rvUrgent.visibility = View.GONE
        }

        if (warningPots.isNotEmpty()) {
            binding.tvWarningTitle.visibility = View.VISIBLE
            binding.rvWarning.visibility = View.VISIBLE
            warningAdapter.submitList(warningPots)
        } else {
            binding.tvWarningTitle.visibility = View.GONE
            binding.rvWarning.visibility = View.GONE
        }

        if (safePots.isNotEmpty()) {
            binding.tvSafeTitle.visibility = View.VISIBLE
            binding.rvSafe.visibility = View.VISIBLE
            safeAdapter.submitList(safePots)
        } else {
            binding.tvSafeTitle.visibility = View.GONE
            binding.rvSafe.visibility = View.GONE
        }

        // Show empty state if no pots at all
        if (pots.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.tvEmptyState.visibility = View.GONE
        }
    }

    private fun loadData() {
        val token = preferenceManager.getAuthToken().orEmpty()
        viewModel.loadHydrationPots(token)
    }

    private fun showLoadingOverlay() {
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    private fun hideLoadingOverlay() {
        binding.loadingOverlay.visibility = View.GONE
    }
}

