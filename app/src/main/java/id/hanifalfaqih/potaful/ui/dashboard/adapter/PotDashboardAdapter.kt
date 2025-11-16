package id.hanifalfaqih.potaful.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.remote.response.PotDetailData
import id.hanifalfaqih.potaful.data.remote.response.PotItem
import id.hanifalfaqih.potaful.databinding.ItemAddPotBinding
import id.hanifalfaqih.potaful.databinding.ItemPotDashboardBinding
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
            binding.tvPotName.text = item.typeName
            binding.tvSoilHealthPercentage.text = formatFloat(item.soilHealth) + "%"

            // Check if item is expanded
            val detailData = expandedItems[item.potId]
            val isExpanded = detailData != null

            if (isExpanded && detailData != null) {
                // Show expanded data
                binding.gridParameters.visibility = View.VISIBLE
                binding.ivDropdownIcon.setImageResource(R.drawable.ic_show_up)
                bindDetailData(detailData)
            } else {
                // Show collapsed
                binding.gridParameters.visibility = View.GONE
                binding.ivDropdownIcon.setImageResource(R.drawable.ic_show_down)
                // Show basic water level - calculate from max_water and soil_health
                val currentWater = item.maxWater * (item.soilHealth / 100.0f)
                binding.tvWaterLevel.text =
                    String.format(Locale.getDefault(), "%.1fL", currentWater)
            }

            // Click to expand/collapse
            binding.root.setOnClickListener { onItemClick(item) }
        }

        private fun bindDetailData(data: PotDetailData) {
            val sensorData = data.sensorData

            // Water level
            binding.tvWaterLevel.text = formatFloat(sensorData.waterLevel) + "%"

            // Salinitas
            binding.tvSalinitasValue.text = formatFloat(sensorData.salinity)

            // Konduktivitas (EC)
            binding.tvEcValue.text = formatFloat(sensorData.conductivity)

            // Nitrogen (Int)
            binding.tvNitrogenValue.text = sensorData.nitrogen.toString()

            // Fosfor (Int)
            binding.tvFosforValue.text = sensorData.phosphorus.toString()

            // Kalium (Int)
            binding.tvKaliumValue.text = sensorData.kalium.toString()

            // pH
            binding.tvPhValue.text = formatFloat(sensorData.ph)

            // Kelembapan
            binding.tvKelembapanValue.text = formatFloat(sensorData.moisture)

            // Suhu
            binding.tvSuhuValue.text = formatFloat(sensorData.temperature)
        }

        private fun formatNumber(value: Double): String {
            return if (value % 1.0 == 0.0) {
                String.format(Locale.getDefault(), "%.0f", value)
            } else {
                String.format(Locale.getDefault(), "%.1f", value)
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
        notifyDataSetChanged()
    }

    fun updateExpandedItem(potId: String, detailData: PotDetailData?) {
        if (detailData != null) {
            expandedItems[potId] = detailData
        } else {
            expandedItems.remove(potId)
        }
        // Find position and notify
        val position = items.indexOfFirst { it.potId == potId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }

    fun isExpanded(potId: String): Boolean {
        return expandedItems.containsKey(potId)
    }
}

