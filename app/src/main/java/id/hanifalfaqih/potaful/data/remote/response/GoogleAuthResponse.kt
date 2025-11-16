package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class GoogleAuthUrlResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: GoogleAuthUrlData
)

data class GoogleAuthUrlData(
    @SerializedName("auth_url") val authUrl: String
)

