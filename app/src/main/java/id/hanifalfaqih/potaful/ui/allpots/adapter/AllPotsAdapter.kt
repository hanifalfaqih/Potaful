package id.hanifalfaqih.potaful.ui.allpots.adapter

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.hanifalfaqih.potaful.R
import id.hanifalfaqih.potaful.data.remote.response.HydrationPotItem
import id.hanifalfaqih.potaful.databinding.ItemPotAllBinding

class AllPotsAdapter(
    private val items: MutableList<HydrationPotItem> = mutableListOf(),
    private val onWaterClick: (HydrationPotItem) -> Unit
) : RecyclerView.Adapter<AllPotsAdapter.AllPotsViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<HydrationPotItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPotsViewHolder {
        val binding = ItemPotAllBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AllPotsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllPotsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class AllPotsViewHolder(private val binding: ItemPotAllBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HydrationPotItem) {
            binding.tvPotName.text = item.typeName ?: "Unknown Pot"
            binding.tvSoilHydration.text = "${item.soilHydration ?: 0}%"

            // Set status badge and colors based on condition
            val condition = item.condition?.uppercase() ?: "SAFE"
            binding.chipStatusBadge.text = condition

            val context = binding.root.context
            when (condition) {
                "Urgent" -> {
                    binding.statusIndicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_bahaya_bg)
                    )
                    binding.chipStatusBadge.apply {
                        chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.status_bahaya_bg)
                        )
                        setTextColor(
                            ContextCompat.getColor(context, R.color.status_bahaya_text)
                        )
                    }
                }

                "Warning" -> {
                    binding.statusIndicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_perlu_perhatian_bg)
                    )
                    binding.chipStatusBadge.apply {
                        chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.status_perlu_perhatian_bg)
                        )
                        setTextColor(
                            ContextCompat.getColor(context, R.color.status_perlu_perhatian_text)
                        )
                    }
                }

                "Safe" -> {
                    binding.statusIndicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.status_baik_bg)
                    )
                    binding.chipStatusBadge.apply {
                        chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.status_baik_bg)
                        )
                        setTextColor(
                            ContextCompat.getColor(context, R.color.status_baik_text)
                        )
                    }
                }

                else -> {
                    binding.statusIndicator.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.forest_green_medium)
                    )
                    binding.chipStatusBadge.apply {
                        chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                            ContextCompat.getColor(context, R.color.forest_green_medium)
                        )
                        setTextColor(
                            ContextCompat.getColor(context, R.color.white)
                        )
                    }
                }
            }

            // Water button click
            binding.btnWater.setOnClickListener { view ->
                if (item.id != null) {
                    // Haptic feedback
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

                    // Scale animation
                    view.animate()
                        .scaleX(0.9f)
                        .scaleY(0.9f)
                        .setDuration(100)
                        .withEndAction {
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
        }
    }
}

