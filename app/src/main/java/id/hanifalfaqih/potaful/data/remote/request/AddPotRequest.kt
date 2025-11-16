package id.hanifalfaqih.potaful.data.remote.request

import com.google.gson.annotations.SerializedName

data class AddPotRequest(
    @SerializedName("pot_id")
    val potId: String
)

