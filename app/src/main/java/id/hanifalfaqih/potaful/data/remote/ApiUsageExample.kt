package id.hanifalfaqih.potaful.data.remote

/**
 * Example Usage of API Configuration
 *
 * To use the API in your Activity or ViewModel, follow these examples:
 *
 * 1. Basic Setup:
 * ```kotlin
 * import id.hanifalfaqih.potaful.data.remote.ApiConfig
 * import id.hanifalfaqih.potaful.data.repository.ApiRepository
 * import kotlinx.coroutines.launch
 * import androidx.lifecycle.lifecycleScope // For Activity
 *
 * class YourActivity : AppCompatActivity() {
 *     private lateinit var repository: ApiRepository
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         // Initialize repository
 *         val apiService = ApiConfig.getApiService()
 *         repository = ApiRepository(apiService)
 *     }
 * }
 * ```
 *
 * 2. Making API Calls (Login Example):
 * ```kotlin
 * private fun login(email: String, password: String) {
 *     lifecycleScope.launch {
 *         when (val result = repository.login(email, password)) {
 *             is Result.Success -> {
 *                 // Handle success
 *                 val data = result.data
 *                 Toast.makeText(this@YourActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Error -> {
 *                 // Handle error
 *                 Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Loading -> {
 *                 // Show loading
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 3. Making API Calls with Token (Get Profile Example):
 * ```kotlin
 * private fun getProfile() {
 *     val token = "your_auth_token_here" // Get from SharedPreferences or DataStore
 *
 *     lifecycleScope.launch {
 *         when (val result = repository.getProfile(token)) {
 *             is Result.Success -> {
 *                 // Handle success
 *                 val profileData = result.data
 *                 // Update UI with profile data
 *             }
 *             is Result.Error -> {
 *                 // Handle error
 *                 Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Loading -> {
 *                 // Show loading
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 4. With Progress Dialog:
 * ```kotlin
 * private fun loginWithLoading(email: String, password: String) {
 *     val progressDialog = ProgressDialog(this).apply {
 *         setMessage("Loading...")
 *         setCancelable(false)
 *         show()
 *     }
 *
 *     lifecycleScope.launch {
 *         when (val result = repository.login(email, password)) {
 *             is Result.Success -> {
 *                 progressDialog.dismiss()
 *                 Toast.makeText(this@YourActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Error -> {
 *                 progressDialog.dismiss()
 *                 Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Loading -> {
 *                 // Already showing
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 5. Custom Request Body Example:
 * ```kotlin
 * // Create data class for request
 * data class PlantRequest(
 *     val name: String,
 *     val type: String,
 *     val wateringFrequency: Int
 * )
 *
 * private fun addPlant() {
 *     val token = "your_auth_token_here"
 *     val plantData = PlantRequest(
 *         name = "Monstera",
 *         type = "Indoor",
 *         wateringFrequency = 7
 *     )
 *
 *     lifecycleScope.launch {
 *         when (val result = repository.addPlant(token, plantData)) {
 *             is Result.Success -> {
 *                 Toast.makeText(this@YourActivity, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Error -> {
 *                 Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
 *             }
 *             is Result.Loading -> {
 *                 // Show loading
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * Note: Don't forget to add these dependencies in your Activity:
 * ```kotlin
 * import kotlinx.coroutines.launch
 * import androidx.lifecycle.lifecycleScope
 * ```
 */

