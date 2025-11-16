package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T? = null
)

data class ErrorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("errors")
    val errors: Map<String, List<String>>? = null
)

