package id.hanifalfaqih.potaful.data.remote

/**
 * Example Usage for My Pots API
 *
 * Response Structure:
 * {
 *     "status": "SUCCESS",
 *     "message": "Data pot berhasil diambil",
 *     "data": {
 *         "pots": [
 *             {
 *                 "pot_id": "t8NTt3FhUZ",
 *                 "type_name": "Potafull Home 1.0",
 *                 "max_water": 6,
 *                 "soil_health": 0,
 *                 "last_update": "2025-11-14T12:25:01.254Z"
 *             }
 *         ],
 *         "total": 1
 *     }
 * }
 *
 * Usage in Activity/Fragment:
 *
 * ```kotlin
 * import androidx.lifecycle.lifecycleScope
 * import kotlinx.coroutines.launch
 * import id.hanifalfaqih.potaful.data.remote.ApiConfig
 * import id.hanifalfaqih.potaful.data.repository.ApiRepository
 * import id.hanifalfaqih.potaful.data.local.PreferenceManager
 * import id.hanifalfaqih.potaful.data.remote.Result
 * import android.widget.Toast
 *
 * class MyPotsActivity : AppCompatActivity() {
 *
 *     private lateinit var repository: ApiRepository
 *     private lateinit var preferenceManager: PreferenceManager
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         // Initialize
 *         val apiService = ApiConfig.getApiService()
 *         repository = ApiRepository(apiService)
 *         preferenceManager = PreferenceManager(this)
 *
 *         // Load pots
 *         loadMyPots()
 *     }
 *
 *     private fun loadMyPots() {
 *         val token = preferenceManager.getAuthToken()
 *
 *         if (token.isNullOrEmpty()) {
 *             Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
 *             return
 *         }
 *
 *         lifecycleScope.launch {
 *             when (val result = repository.getMyPots(token)) {
 *                 is Result.Success -> {
 *                     val response = result.data
 *
 *                     // Check status
 *                     if (response.status == "SUCCESS") {
 *                         val pots = response.data.pots
 *                         val total = response.data.total
 *
 *                         // Update UI with pots data
 *                         pots.forEach { pot ->
 *                             println("Pot ID: ${pot.potId}")
 *                             println("Type: ${pot.typeName}")
 *                             println("Max Water: ${pot.maxWater}")
 *                             println("Soil Health: ${pot.soilHealth}")
 *                             println("Last Update: ${pot.lastUpdate}")
 *                         }
 *
 *                         // Display in RecyclerView
 *                         // adapter.submitList(pots)
 *
 *                         Toast.makeText(this@MyPotsActivity,
 *                             "Berhasil memuat $total pot",
 *                             Toast.LENGTH_SHORT).show()
 *                     }
 *                 }
 *                 is Result.Error -> {
 *                     Toast.makeText(this@MyPotsActivity,
 *                         "Error: ${result.message}",
 *                         Toast.LENGTH_SHORT).show()
 *                 }
 *                 is Result.Loading -> {
 *                     // Show loading indicator
 *                 }
 *             }
 *         }
 *     }
 *
 *     // Example: Display in RecyclerView
 *     private fun setupRecyclerView(pots: List<PotItem>) {
 *         val adapter = MyPotsAdapter(pots) { pot ->
 *             // Handle pot click
 *             Toast.makeText(this, "Clicked: ${pot.typeName}", Toast.LENGTH_SHORT).show()
 *         }
 *
 *         recyclerView.adapter = adapter
 *         recyclerView.layoutManager = LinearLayoutManager(this)
 *     }
 *
 *     // Example: Format date
 *     private fun formatDate(dateString: String): String {
 *         return try {
 *             val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
 *             inputFormat.timeZone = TimeZone.getTimeZone("UTC")
 *             val date = inputFormat.parse(dateString)
 *
 *             val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
 *             date?.let { outputFormat.format(it) } ?: dateString
 *         } catch (e: Exception) {
 *             dateString
 *         }
 *     }
 * }
 *
 * // Example RecyclerView Adapter
 * class MyPotsAdapter(
 *     private val pots: List<PotItem>,
 *     private val onItemClick: (PotItem) -> Unit
 * ) : RecyclerView.Adapter<MyPotsAdapter.ViewHolder>() {
 *
 *     class ViewHolder(val binding: ItemPotBinding) : RecyclerView.ViewHolder(binding.root)
 *
 *     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
 *         val binding = ItemPotBinding.inflate(
 *             LayoutInflater.from(parent.context), parent, false
 *         )
 *         return ViewHolder(binding)
 *     }
 *
 *     override fun onBindViewHolder(holder: ViewHolder, position: Int) {
 *         val pot = pots[position]
 *
 *         holder.binding.apply {
 *             tvPotName.text = pot.typeName
 *             tvPotId.text = "ID: ${pot.potId}"
 *             tvMaxWater.text = "Max Water: ${pot.maxWater}L"
 *             tvSoilHealth.text = "Soil Health: ${pot.soilHealth}%"
 *
 *             // Set progress
 *             progressSoilHealth.progress = pot.soilHealth
 *
 *             root.setOnClickListener { onItemClick(pot) }
 *         }
 *     }
 *
 *     override fun getItemCount() = pots.size
 * }
 * ```
 *
 * With Loading Dialog:
 * ```kotlin
 * private fun loadMyPotsWithLoading() {
 *     val token = preferenceManager.getAuthToken() ?: return
 *
 *     val progressDialog = ProgressDialog(this).apply {
 *         setMessage("Memuat data pot...")
 *         setCancelable(false)
 *         show()
 *     }
 *
 *     lifecycleScope.launch {
 *         when (val result = repository.getMyPots(token)) {
 *             is Result.Success -> {
 *                 progressDialog.dismiss()
 *
 *                 val pots = result.data.data.pots
 *                 setupRecyclerView(pots)
 *             }
 *             is Result.Error -> {
 *                 progressDialog.dismiss()
 *                 Toast.makeText(this@MyPotsActivity, result.message, Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Loading -> {
 *                 // Already showing
 *             }
 *         }
 *     }
 * }
 * ```
 */

