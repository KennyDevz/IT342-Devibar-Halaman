package edu.cit.devibar.halaman.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.CareLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TimelineAdapter(private var images: List<CareLog>) : RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>() {

    class TimelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSnapshot: ImageView = view.findViewById(R.id.ivSnapshot)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val vLineTop: View = view.findViewById(R.id.vLineTop)
        val vLineBottom: View = view.findViewById(R.id.vLineBottom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline_card, parent, false)
        return TimelineViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {
        val item = images[position]
        
        holder.tvDate.text = formatDisplayDate(item.timestamp)
        holder.tvDescription.text = item.note ?: "Growth Milestone"
        
        holder.ivSnapshot.load(item.id) { // item.id contains the image URL in this mapping
             crossfade(true)
             placeholder(R.drawable.bg_glass_icon)
             error(R.drawable.bg_glass_icon)
        }

        // Logic to hide lines for first/last items to create a clean start and end
        holder.vLineTop.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
        holder.vLineBottom.visibility = if (position == itemCount - 1) View.INVISIBLE else View.VISIBLE
    }

    override fun getItemCount() = images.size

    fun updateData(newImages: List<CareLog>) {
        this.images = newImages
        notifyDataSetChanged()
    }

    private fun formatDisplayDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        return try {
            val dateTime = LocalDateTime.parse(dateString.split(".")[0])
            dateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))
        } catch (e: Exception) {
            dateString
        }
    }
}
