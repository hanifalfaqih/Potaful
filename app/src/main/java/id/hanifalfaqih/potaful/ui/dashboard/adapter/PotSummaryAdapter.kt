package id.hanifalfaqih.potaful.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.hanifalfaqih.potaful.data.remote.response.PotItem
import id.hanifalfaqih.potaful.databinding.ItemPotSummaryBinding

class PotSummaryAdapter(
    private val items: MutableList<PotItem> = mutableListOf(),
    private val onWaterClick: (PotItem) -> Unit,
    private val onItemClick: (PotItem) -> Unit
) : RecyclerView.Adapter<PotSummaryAdapter.PotSummaryViewHolder>() {

    inner class PotSummaryViewHolder(val binding: ItemPotSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PotItem) {
            binding.tvPotSummaryName.text = item.typeName
            binding.btnSiram.setOnClickListener { onWaterClick(item) }
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotSummaryViewHolder {
        val binding =
            ItemPotSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PotSummaryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PotSummaryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<PotItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

