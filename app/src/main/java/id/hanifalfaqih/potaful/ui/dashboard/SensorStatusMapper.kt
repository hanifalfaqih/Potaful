package id.hanifalfaqih.potaful.ui.dashboard

import android.content.Context
import id.hanifalfaqih.potaful.R

object SensorStatusMapper {
    data class Status(val key: String, val label: String, val bgColor: Int, val textColor: Int)

    private fun build(context: Context, key: String, label: String, bg: Int, text: Int) =
        Status(key, label, context.getColor(bg), context.getColor(text))

    private fun statusBaik(context: Context) =
        build(context, "baik", "Good", R.color.status_baik_bg, R.color.status_baik_text)

    private fun statusCukup(context: Context) =
        build(context, "cukup", "Fair", R.color.status_cukup_bg, R.color.status_cukup_text)

    private fun statusPerluPerhatian(context: Context) = build(
        context,
        "perlu_perhatian",
        "Needs Attention",
        R.color.status_perlu_perhatian_bg,
        R.color.status_perlu_perhatian_text
    )

    private fun statusBahaya(context: Context) =
        build(context, "bahaya", "Critical", R.color.status_bahaya_bg, R.color.status_bahaya_text)

    fun mapPh(context: Context, value: Float): Status = when {
        value in 5.8f..6.5f -> statusBaik(context)
        (value in 5.5f..5.79f) || (value in 6.51f..6.8f) -> statusCukup(context)
        (value in 5.2f..5.49f) || (value in 6.81f..7.2f) -> statusPerluPerhatian(context)
        value < 5.2f || value > 7.2f -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapMoisture(context: Context, value: Float): Status = when {
        value in 60f..75f -> statusBaik(context)
        (value in 50f..59.9f) || (value in 75.1f..80f) -> statusCukup(context)
        (value in 40f..49.9f) || (value in 80.1f..90f) -> statusPerluPerhatian(context)
        value < 40f || value > 90f -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapSoilHumidity(context: Context, value: Float): Status = when {
        value in 60f..75f -> statusBaik(context)
        (value in 50f..59.9f) || (value in 75.1f..85f) -> statusCukup(context)
        value < 40f || value > 90f -> statusBahaya(context)
        else -> statusPerluPerhatian(context)
    }

    fun mapNitrogen(context: Context, value: Int): Status = when {
        value in 30..60 -> statusBaik(context)
        (value in 20..29) || (value in 61..80) -> statusCukup(context)
        (value in 10..19) || (value in 81..100) -> statusPerluPerhatian(context)
        value < 10 || value > 100 -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapPhosphorus(context: Context, value: Int): Status = when {
        value in 10..25 -> statusBaik(context)
        (value in 7..9) || (value in 26..40) -> statusCukup(context)
        (value in 5..6) || (value in 41..60) -> statusPerluPerhatian(context)
        value < 5 || value > 60 -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapPotassium(context: Context, value: Int): Status = when {
        value in 150..220 -> statusBaik(context)
        (value in 120..149) || (value in 221..300) -> statusCukup(context)
        (value in 100..119) || (value in 301..400) -> statusPerluPerhatian(context)
        value < 100 || value > 400 -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapConductivity(context: Context, value: Float): Status = when {
        value in 1.2f..2.0f -> statusBaik(context)
        (value in 0.8f..1.19f) || (value in 2.01f..2.3f) -> statusCukup(context)
        (value in 0.5f..0.79f) || (value in 2.31f..2.6f) -> statusPerluPerhatian(context)
        value < 0.5f || value > 2.6f -> statusBahaya(context)
        else -> statusBahaya(context)
    }

    fun mapPlantTemperature(context: Context, value: Float): Status = when {
        value in 20f..30f -> statusBaik(context)
        (value in 18f..19.9f) || (value in 30.1f..32f) -> statusCukup(context)
        (value in 15f..17.9f) || (value in 32.1f..35f) -> statusPerluPerhatian(context)
        value < 15f || value > 35f -> statusBahaya(context)
        else -> statusBahaya(context)
    }
}
