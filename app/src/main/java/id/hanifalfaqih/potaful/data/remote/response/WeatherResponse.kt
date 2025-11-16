package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

// Simplified weather response for needed fields
data class WeatherResponse(
    @SerializedName("weather") val weather: List<WeatherCondition>,
    @SerializedName("main") val main: MainInfo,
    @SerializedName("name") val name: String
) {
    data class WeatherCondition(
        @SerializedName("main") val main: String,
        @SerializedName("description") val description: String,
        @SerializedName("icon") val icon: String
    )

    data class MainInfo(
        @SerializedName("temp") val temp: Double,
        @SerializedName("humidity") val humidity: Int
    )
}
