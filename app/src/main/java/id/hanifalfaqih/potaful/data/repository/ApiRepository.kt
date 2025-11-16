package id.hanifalfaqih.potaful.data.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import id.hanifalfaqih.potaful.data.remote.ApiService
import id.hanifalfaqih.potaful.data.remote.Result
import id.hanifalfaqih.potaful.data.remote.WeatherApiService
import id.hanifalfaqih.potaful.data.remote.request.AddPotRequest
import id.hanifalfaqih.potaful.data.remote.response.GoogleAuthUrlResponse
import id.hanifalfaqih.potaful.data.remote.response.MyPotsResponse
import id.hanifalfaqih.potaful.data.remote.response.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiRepository(private val apiService: ApiService) {

    // Helper function to handle API calls that return response directly
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                Result.Success(response)
            } catch (e: retrofit2.HttpException) {
                val errorMessage = try {
                    val errorBody = e.response()?.errorBody()?.string()
                    if (errorBody != null) {
                        // Try to parse error response as JSON
                        val gson = Gson()
                        val errorResponse = gson.fromJson(errorBody, JsonObject::class.java)
                        errorResponse.get("message")?.asString ?: "Error: ${e.code()}"
                    } else {
                        "Error: ${e.code()}"
                    }
                } catch (parseException: Exception) {
                    "Error: ${e.code()} - ${e.message()}"
                }
                Result.Error(errorMessage)
            } catch (e: Exception) {
                Result.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    // My Pots methods
    suspend fun getMyPots(token: String): Result<MyPotsResponse> {
        return safeApiCall {
            apiService.getMyPots("Bearer $token")
        }
    }

    suspend fun addPot(token: String, potId: String): Result<Any> {
        return safeApiCall {
            val request = AddPotRequest(potId)
            apiService.addPot("Bearer $token", request)
        }
    }

    suspend fun getPotDetail(
        token: String,
        potId: String
    ): Result<id.hanifalfaqih.potaful.data.remote.response.PotDetailResponse> {
        return safeApiCall {
            apiService.getPotDetail("Bearer $token", potId)
        }
    }

    suspend fun getUserProfile(token: String): Result<id.hanifalfaqih.potaful.data.remote.response.UserProfileResponse> {
        return safeApiCall {
            apiService.getUserProfile("Bearer $token")
        }
    }

    suspend fun getGoogleAuthUrl(): Result<GoogleAuthUrlResponse> {
        return safeApiCall { apiService.getGoogleAuthUrl() }
    }

    private val weatherService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun getCurrentWeather(city: String, apiKey: String): Result<WeatherResponse> {
        return safeApiCall { weatherService.getCurrentWeather(city, apiKey) }
    }
}
