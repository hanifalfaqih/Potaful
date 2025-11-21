package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

// Response list hydration untuk rv_summary:
// {
//   "status": "SUCCESS",
//   "message": "Data hidrasi pot berhasil diambil",
//   "data": {
//     "pots": [
//       {
//         "id": "t8NTt3FhUZ",
//         "user_id": "...",
//         "type_pot_id": "...",
//         "created_at": "...",
//         "updated_at": "...",
//         "type_name": "Potafull Home 1.0",
//         "max_water": 6,
//         "condition": "URGENT",
//         "soil_hydration": 0
//       }
//     ],
//     "total": 1
//   }
// }

data class HydrationListResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: HydrationData
)

data class HydrationData(
    @SerializedName("pots")
    val pots: List<HydrationPotItem>,

    @SerializedName("total")
    val total: Int
)

data class HydrationPotItem(
    @SerializedName("id")
    val id: String?,

    @SerializedName("user_id")
    val userId: String?,

    @SerializedName("type_pot_id")
    val typePotId: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("type_name")
    val typeName: String?,

    @SerializedName("max_water")
    val maxWater: Int?,

    @SerializedName("condition")
    val condition: String?,

    @SerializedName("soil_hydration")
    val soilHydration: Float?
)

