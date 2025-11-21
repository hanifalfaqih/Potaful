package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class PlantRecommendationResponse(
    @SerializedName("output")
    val output: List<Output>
)

data class Output(
    @SerializedName("role")
    val role: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("content")
    val content: List<Content>,
    @SerializedName("id")
    val id: String
)

data class Content(
    @SerializedName("type")
    val type: String,
    @SerializedName("text")
    val text: TextData,
    @SerializedName("annotations")
    val annotations: List<Any>
)

data class TextData(
    @SerializedName("recommendation")
    val recommendation: List<String>,
    @SerializedName("reason")
    val reason: List<String>
)

