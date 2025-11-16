# Quick Reference - My Pots API

## ⚡ Quick Copy-Paste Code

### 1. Initialize in Activity

```kotlin
// Add these properties to your Activity
private lateinit var repository: ApiRepository
private lateinit var preferenceManager: PreferenceManager

// In onCreate()
val apiService = ApiConfig.getApiService()
repository = ApiRepository(apiService)
preferenceManager = PreferenceManager(this)
```

### 2. Load My Pots (Simple)

```kotlin
private fun loadMyPots() {
    val token = preferenceManager.getAuthToken() ?: return

    lifecycleScope.launch {
        when (val result = repository.getMyPots(token)) {
            is Result.Success -> {
                val pots = result.data.data.pots
                // Use pots data here
            }
            is Result.Error -> {
                Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {}
        }
    }
}
```

### 3. Required Imports

```kotlin
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.repository.ApiRepository
import id.hanifalfaqih.potaful.data.local.PreferenceManager
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.remote.response.PotItem
```

---

## 📊 Response Data Access

```kotlin
when (val result = repository.getMyPots(token)) {
    is Result.Success -> {
        val response = result.data

        response.status              // "SUCCESS"
        response.message             // "Data pot berhasil diambil"
        response.data.pots           // List<PotItem>
        response.data.total          // Int (total count)

        // Access individual pot
        response.data.pots.forEach { pot ->
            pot.potId                // "t8NTt3FhUZ"
            pot.typeName             // "Potafull Home 1.0"
            pot.maxWater             // 6
            pot.soilHealth           // 0
            pot.lastUpdate           // "2025-11-14T12:25:01.254Z"
        }
    }
}
```

---

## 🎯 Common Use Cases

### Check if pots list is empty

```kotlin
is Result.Success -> {
    val pots = result.data.data.pots
    if (pots.isEmpty()) {
        // Show empty state
        tvEmptyState.visibility = View.VISIBLE
    } else {
        // Show pots list
        tvEmptyState.visibility = View.GONE
        adapter.submitList(pots)
    }
}
```

### Show total count

```kotlin
is Result.Success -> {
    val total = result.data.data.total
    tvTotalPots.text = "Total: $total pot(s)"
}
```

### Format last update date

```kotlin
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(dateString)

        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}

// Usage
tvLastUpdate.text = formatDate(pot.lastUpdate)
// Output: "14 Nov 2025, 12:25"
```

### Calculate water percentage

```kotlin
private fun getWaterPercentage(soilHealth: Int): Int {
    return soilHealth // Already in percentage (0-100)
}

// Usage
progressBar.progress = pot.soilHealth
tvWaterLevel.text = "${pot.soilHealth}%"
```

---

## 🔄 Pull to Refresh

```kotlin
swipeRefreshLayout.setOnRefreshListener {
    loadMyPots()
}

// In loadMyPots(), add:
is Result.Success -> {
    swipeRefreshLayout.isRefreshing = false
    // ... rest of code
}
is Result.Error -> {
    swipeRefreshLayout.isRefreshing = false
    // ... rest of code
}
```

---

## 🎨 Material Design Loading

```kotlin
// Using Material CircularProgressIndicator
private fun showLoading(isLoading: Boolean) {
    progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
}

private fun loadMyPots() {
    val token = preferenceManager.getAuthToken() ?: return

    showLoading(true)

    lifecycleScope.launch {
        when (val result = repository.getMyPots(token)) {
            is Result.Success -> {
                showLoading(false)
                // Handle success
            }
            is Result.Error -> {
                showLoading(false)
                // Handle error
            }
            is Result.Loading -> {}
        }
    }
}
```

---

## 🚨 Error Handling Best Practices

```kotlin
when (val result = repository.getMyPots(token)) {
    is Result.Success -> {
        if (result.data.status == "SUCCESS") {
            // Success
            val pots = result.data.data.pots
            displayPots(pots)
        } else {
            // API returned but not success
            Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
        }
    }
    is Result.Error -> {
        // Network error or HTTP error
        val errorMessage = when {
            result.message.contains("Unable to resolve host") ->
                "No internet connection"
            result.message.contains("401") ->
                "Session expired, please login again"
            result.message.contains("500") ->
                "Server error, please try again later"
            else ->
                result.message
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
    is Result.Loading -> {
        // Show loading
    }
}
```

---

## 💾 Save Token Example

```kotlin
// After login success
preferenceManager.saveAuthToken("your_token_here")
preferenceManager.setLoggedIn(true)

// Get token
val token = preferenceManager.getAuthToken()

// Clear on logout
preferenceManager.clearUserData()
```

---

## 🎯 Files You Need

✅ **Already Created:**

- `ApiConfig.kt` - Retrofit setup
- `ApiService.kt` - API endpoints
- `ApiRepository.kt` - Repository layer
- `Result.kt` - Result wrapper
- `PotResponse.kt` - Response models
- `PreferenceManager.kt` - Token storage

📝 **You Need to Create:**

- Activity/Fragment untuk display pots
- RecyclerView Adapter (optional)
- Item layout XML

---

## 🎬 Complete Minimal Example

```kotlin
class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: ApiRepository
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize
        val apiService = ApiConfig.getApiService()
        repository = ApiRepository(apiService)
        preferenceManager = PreferenceManager(this)

        // Load pots
        loadMyPots()
    }

    private fun loadMyPots() {
        val token = preferenceManager.getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            when (val result = repository.getMyPots(token)) {
                is Result.Success -> {
                    val pots = result.data.data.pots
                    Toast.makeText(
                        this@DashboardActivity,
                        "Loaded ${pots.size} pots",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Error -> {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Loading -> {}
            }
        }
    }
}
```

---

## ✅ Checklist

- [x] Dependencies added to gradle
- [x] Response models created
- [x] API service defined
- [x] Repository implemented
- [x] PreferenceManager for token
- [ ] Sync Gradle project
- [ ] Get auth token from login
- [ ] Call API in your Activity
- [ ] Display data in UI

---

**Next Step**: Sync Gradle dan implement di Activity kamu! 🚀

