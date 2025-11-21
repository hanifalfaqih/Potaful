package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

// Response untuk perintah penyiraman pot
// Contoh JSON:
// {
//   "status": "SUCCESS",
//   "message": "Perintah penyiraman berhasil dikirim",
//   "data": {
//     "pot_id": "t8NTt3FhUZ",
//     "action": "watering",
//     "status": "sent"
//   }
// }

data class WateringResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: WateringData
)

data class WateringData(
    @SerializedName("pot_id") val potId: String,
    @SerializedName("action") val action: String,
    @SerializedName("status") val status: String
)

