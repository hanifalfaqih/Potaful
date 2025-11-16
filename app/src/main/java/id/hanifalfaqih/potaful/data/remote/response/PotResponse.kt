package id.hanifalfaqih.potaful.data.remote.response

import com.google.gson.annotations.SerializedName

data class MyPotsResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: PotsData
)

data class PotsData(
    @SerializedName("pots")
    val pots: List<PotItem>,

    @SerializedName("total")
    val total: Int
)

data class PotItem(
    @SerializedName("pot_id")
    val potId: String,

    @SerializedName("type_name")
    val typeName: String,

    @SerializedName("max_water")
    val maxWater: Int,

    @SerializedName("soil_health")
    val soilHealth: Float,

    @SerializedName("last_update")
    val lastUpdate: String
)

data class PotDetailResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: PotDetailData
)

data class PotDetailData(
    @SerializedName("pot_id")
    val potId: String,

    @SerializedName("type_name")
    val typeName: String,

    @SerializedName("max_water")
    val maxWater: Int,

    @SerializedName("sensor_data")
    val sensorData: SensorData,

    @SerializedName("timestamp")
    val timestamp: String
)

data class SensorData(
    @SerializedName("n")
    val nitrogen: Int,

    @SerializedName("p")
    val phosphorus: Int,

    @SerializedName("k")
    val kalium: Int,

    @SerializedName("temperature")
    val temperature: Float,

    @SerializedName("moisture")
    val moisture: Float,

    @SerializedName("ph")
    val ph: Float,

    @SerializedName("salinity")
    val salinity: Float,

    @SerializedName("conductivity")
    val conductivity: Float,

    @SerializedName("water_level")
    val waterLevel: Float,

    @SerializedName("soil_health")
    val soilHealth: Float
)

