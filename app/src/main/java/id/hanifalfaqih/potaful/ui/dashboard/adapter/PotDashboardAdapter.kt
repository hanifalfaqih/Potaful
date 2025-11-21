package id.hanifalfaqih.potaful.ui.dashboard.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.remote.response.PotDetailData
import id.hanifalfaqih.potaful.data.remote.response.PotItem
import id.hanifalfaqih.potaful.databinding.ItemAddPotBinding
import id.hanifalfaqih.potaful.databinding.ItemPotDashboardBinding
import id.hanifalfaqih.potaful.ui.dashboard.SensorStatusMapper
import java.util.Locale

class PotDashboardAdapter(
    val items: MutableList<PotItem> = mutableListOf(),
    private val expandedItems: MutableMap<String, PotDetailData> = mutableMapOf(),
    private val onItemClick: (PotItem) -> Unit,
    private val onAddPotClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_POT = 0
        private const val VIEW_TYPE_ADD = 1
    }

    inner class PotDashboardViewHolder(val binding: ItemPotDashboardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PotItem) {
            // Basic info
            binding.tvPotName.text = item.typeName ?: "Unknown Pot"
            binding.tvSoilHealthPercentage.text =
                String.format(Locale.getDefault(), "%s%%", formatFloat(item.soilHealth ?: 0f))

            // Determine expanded state directly
            val detailData = item.potId?.let { expandedItems[it] }

            if (detailData != null) {
                // Expanded
                binding.gridParameters.visibility = View.VISIBLE
                binding.ivDropdownIcon.setImageResource(R.drawable.ic_show_up)
                bindDetailData(detailData)
            } else {
                // Collapsed
                binding.gridParameters.visibility = View.GONE
                binding.ivDropdownIcon.setImageResource(R.drawable.ic_show_down)
                binding.tvWaterLevel.text =
                    String.format(Locale.getDefault(), "%.2fL", item.waterLevel ?: 0f)
            }

            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun bindDetailData(data: PotDetailData) {
            val sensorData = data.sensorData

            binding.tvWaterLevel.text =
                String.format(Locale.getDefault(), "%.2fL", sensorData.waterLevel)

            // Prepare chip view lookups (safer than direct binding if nested deeply)
            val chipSalinity = binding.root.findViewById<Chip>(R.id.chip_salinity)
            val chipConductivity = binding.root.findViewById<Chip>(R.id.chip_conductivity)
            val chipNitrogen = binding.root.findViewById<Chip>(R.id.chip_nitrogen)
            val chipFosfor = binding.root.findViewById<Chip>(R.id.chip_fosfor)
            val chipKalium = binding.root.findViewById<Chip>(R.id.chip_kalium)
            val chipPh = binding.root.findViewById<Chip>(R.id.chip_soil_ph)
            val chipMoisture = binding.root.findViewById<Chip>(R.id.chip_soil_moisture)
            val chipTemperature = binding.root.findViewById<Chip>(R.id.chip_plant_temperature)

            // Salinity
            binding.tvSalinitasValue.text = formatFloat(sensorData.salinity)
            SensorStatusMapper.mapConductivity(binding.root.context, sensorData.salinity)
                .also { status ->
                    chipSalinity?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // Conductivity
            binding.tvEcValue.text = formatFloat(sensorData.conductivity)
            SensorStatusMapper.mapConductivity(binding.root.context, sensorData.conductivity)
                .also { status ->
                    chipConductivity?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // Nitrogen
            binding.tvNitrogenValue.text = sensorData.nitrogen.toString()
            SensorStatusMapper.mapNitrogen(binding.root.context, sensorData.nitrogen)
                .also { status ->
                    chipNitrogen?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // Phosphorus
            binding.tvFosforValue.text = sensorData.phosphorus.toString()
            SensorStatusMapper.mapPhosphorus(binding.root.context, sensorData.phosphorus)
                .also { status ->
                    chipFosfor?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // Potassium
            binding.tvKaliumValue.text = sensorData.kalium.toString()
            SensorStatusMapper.mapPotassium(binding.root.context, sensorData.kalium)
                .also { status ->
                    chipKalium?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // pH
            binding.tvPhValue.text = formatFloat(sensorData.ph)
            SensorStatusMapper.mapPh(binding.root.context, sensorData.ph).also { status ->
                chipPh?.apply {
                    text = status.label
                    setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                    setTextColor(status.textColor)
                }
            }
            // Moisture
            binding.tvKelembapanValue.text = formatFloat(sensorData.moisture)
            SensorStatusMapper.mapMoisture(binding.root.context, sensorData.moisture)
                .also { status ->
                    chipMoisture?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
                }
            // Temperature
            binding.tvSuhuValue.text = formatFloat(sensorData.temperature)
            SensorStatusMapper.mapPlantTemperature(binding.root.context, sensorData.temperature)
                .also { status ->
                    chipTemperature?.apply {
                        text = status.label
                        setChipBackgroundColor(ColorStateList.valueOf(status.bgColor))
                        setTextColor(status.textColor)
                    }
            }
        }

        private fun formatFloat(value: Float): String {
            return if (value % 1.0f == 0.0f) {
                String.format(Locale.getDefault(), "%.0f", value)
            } else {
                String.format(Locale.getDefault(), "%.1f", value)
            }
        }
    }

    inner class AddPotViewHolder(val binding: ItemAddPotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener { onAddPotClick() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < items.size) VIEW_TYPE_POT else VIEW_TYPE_ADD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_POT -> {
                val binding = ItemPotDashboardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PotDashboardViewHolder(binding)
            }
            VIEW_TYPE_ADD -> {
                val binding =
                    ItemAddPotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AddPotViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PotDashboardViewHolder -> holder.bind(items[position])
            is AddPotViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size + 1 // +1 for add button

    fun submitList(newItems: List<PotItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // Could optimize with DiffUtil later
    }

    fun updateExpandedItem(potId: String, detailData: PotDetailData?) {
        if (detailData != null) {
            expandedItems[potId] = detailData
        } else {
            expandedItems.remove(potId)
        }
        val position = items.indexOfFirst { it.potId == potId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    fun isExpanded(potId: String): Boolean = expandedItems.containsKey(potId)
}
