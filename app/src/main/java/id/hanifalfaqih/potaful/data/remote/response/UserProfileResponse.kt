package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserProfileDataWrapper
)

data class UserProfileDataWrapper(
    @SerializedName("user")
    val user: UserProfileData
)

data class UserProfileData(
    @SerializedName("id")
    val id: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("photo")
    val photo: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)

