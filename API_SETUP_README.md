# API Configuration Setup - Potaful

## Base URL

```
https://api.lutfialvarop.cloud/
```

## File Structure

```
app/src/main/java/id/hanifalfaqih/potaful/
├── data/
│   ├── remote/
│   │   ├── ApiConfig.kt          # Retrofit configuration
│   │   ├── ApiService.kt         # API endpoints definition
│   │   ├── Result.kt             # Result wrapper for API responses
│   │   ├── ApiUsageExample.kt    # Example usage guide
│   │   └── response/
│   │       └── BaseResponse.kt   # Base response models
│   └── repository/
│       └── ApiRepository.kt      # Repository layer for API calls
```

## Dependencies Added

The following dependencies have been added to `gradle/libs.versions.toml` and
`app/build.gradle.kts`:

- **Retrofit 2.9.0**: HTTP client for Android and Java
- **OkHttp 4.12.0**: HTTP & HTTP/2 client
- **Gson 2.10.1**: JSON serialization/deserialization
- **Logging Interceptor**: For debugging API calls

## How to Use

### 1. Initialize Repository

```kotlin
import id.hanifalfaqih.potaful.data.remote.ApiConfig
import id.hanifalfaqih.potaful.data.repository.ApiRepository

class YourActivity : AppCompatActivity() {
    private lateinit var repository: ApiRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiConfig.getApiService()
        repository = ApiRepository(apiService)
    }
}
```

### 2. Make API Calls

```kotlin
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

private fun login(email: String, password: String) {
    lifecycleScope.launch {
        when (val result = repository.login(email, password)) {
            is Result.Success -> {
                // Handle success
                Toast.makeText(this@YourActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
            }
            is Result.Error -> {
                // Handle error
                Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                // Show loading indicator
            }
        }
    }
}
```

### 3. API Calls with Authentication Token

```kotlin
private fun getProfile() {
    val token = "your_auth_token_here" // Get from SharedPreferences

    lifecycleScope.launch {
        when (val result = repository.getProfile(token)) {
            is Result.Success -> {
                // Update UI with profile data
            }
            is Result.Error -> {
                Toast.makeText(this@YourActivity, result.message, Toast.LENGTH_SHORT).show()
            }
            is Result.Loading -> {
                // Show loading
            }
        }
    }
}
```

## Available API Methods

### Authentication

- `register(name: String, email: String, password: String)`
- `login(email: String, password: String)`

### User Profile

- `getProfile(token: String)`
- `updateProfile(token: String, profileData: Any)`

### Plants/Pots Management

- `getPlants(token: String)`
- `addPlant(token: String, plantData: Any)`
- `getPlantById(token: String, plantId: String)`
- `updatePlant(token: String, plantId: String, plantData: Any)`
- `deletePlant(token: String, plantId: String)`

## Customizing API Endpoints

To add or modify endpoints, edit the `ApiService.kt` file:

```kotlin
interface ApiService {
    @GET("your/endpoint")
    suspend fun yourMethod(
        @Header("Authorization") token: String
    ): Response<YourResponseType>
}
```

Then add the corresponding method in `ApiRepository.kt`:

```kotlin
suspend fun yourMethod(token: String): Result<YourResponseType> {
    return safeApiCall { apiService.yourMethod("Bearer $token") }
}
```

## Next Steps

1. **Sync Gradle**: Sync your project with Gradle files to download dependencies
2. **Define Response Models**: Create data classes for your API responses in the `response` package
3. **Update Endpoints**: Modify `ApiService.kt` based on your actual API documentation
4. **Store Token**: Implement SharedPreferences or DataStore to save authentication tokens
5. **Test API Calls**: Start implementing API calls in your activities

## Notes

- All API calls are asynchronous and use Kotlin Coroutines
- The `Result` sealed class provides a clean way to handle success, error, and loading states
- Logging interceptor is enabled for debugging (you may want to disable it in production)
- Internet permission has been added to AndroidManifest.xml

## Permissions Added

```xml

<uses-permission android:name="android.permission.INTERNET" /><uses-permission
android:name="android.permission.ACCESS_NETWORK_STATE" />
```

