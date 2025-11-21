package id.hanifalfaqih.potaful.ui.recommendation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.hanifalfaqih.potaful.R

data class PlantRecommendationItem(
    val name: String,
    val reason: String
)

class PlantRecommendationAdapter(
    private val items: List<PlantRecommendationItem>
) : RecyclerView.Adapter<PlantRecommendationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlantName: TextView = view.findViewById(R.id.tv_plant_name)
        val tvPlantReason: TextView = view.findViewById(R.id.tv_plant_reason)
        val ivExpandIcon: ImageView = view.findViewById(R.id.iv_expand_icon)
        val llHeader: LinearLayout = view.findViewById(R.id.ll_header)
        val llExpandableContent: LinearLayout = view.findViewById(R.id.ll_expandable_content)

        var isExpanded = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_recommendation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvPlantName.text = item.name
        holder.tvPlantReason.text = item.reason

        // Set initial state
        holder.llExpandableContent.visibility = if (holder.isExpanded) View.VISIBLE else View.GONE
        holder.ivExpandIcon.setImageResource(
            if (holder.isExpanded) R.drawable.ic_show_up else R.drawable.ic_show_down
        )

        // Handle click to expand/collapse
        holder.llHeader.setOnClickListener {
            holder.isExpanded = !holder.isExpanded

            holder.llExpandableContent.visibility =
                if (holder.isExpanded) View.VISIBLE else View.GONE
            holder.ivExpandIcon.setImageResource(
                if (holder.isExpanded) R.drawable.ic_show_up else R.drawable.ic_show_down
            )
        }
    }

    override fun getItemCount(): Int = items.size
}

