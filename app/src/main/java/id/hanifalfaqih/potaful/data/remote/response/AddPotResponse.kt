package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddPotResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: AddPotData
)

data class AddPotData(
    @SerializedName("pot_id")
    val potId: String,

    @SerializedName("type_pot_id")
    val typePotId: String,

    @SerializedName("type_name")
    val typeName: String,

    @SerializedName("max_water")
    val maxWater: Int,

    @SerializedName("created_at")
    val createdAt: String
)

