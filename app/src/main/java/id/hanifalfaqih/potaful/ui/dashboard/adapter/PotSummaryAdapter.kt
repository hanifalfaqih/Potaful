package id.hanifalfaqih.potaful.ui.dashboard.adapter

import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.hanifalfaqih.potaful.data.remote.response.HydrationPotItem
import id.hanifalfaqih.potaful.databinding.ItemPotSummaryBinding

class PotSummaryAdapter(
    private val items: MutableList<HydrationPotItem> = mutableListOf(),
    private val onWaterClick: (HydrationPotItem) -> Unit,
    private val onItemClick: (HydrationPotItem) -> Unit
) : RecyclerView.Adapter<PotSummaryAdapter.PotSummaryViewHolder>() {

    inner class PotSummaryViewHolder(val binding: ItemPotSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HydrationPotItem) {
            binding.tvPotSummaryName.text = item.typeName ?: "Unknown Pot"

            binding.btnSiram.setOnClickListener { view ->
                if (item.id != null) {
                    // Haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                    // Scale down animation
                    view.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(100)
                        .withEndAction {
                            // Scale back up
                            view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .start()
                        }
                        .start()

                    onWaterClick(item)
                }
            }

            binding.root.setOnClickListener { view ->
                if (item.id != null) {
                    // Haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                    // Scale animation for the whole card
                    view.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .setDuration(80)
                        .withEndAction {
                            view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(80)
                                .start()
                        }
                        .start()

                    onItemClick(item)
                }
            }
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

    fun submitList(newItems: List<HydrationPotItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

