package edu.cit.devibar.halaman.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.CareLog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CareLogAdapter(private var logs: List<CareLog>) : RecyclerView.Adapter<CareLogAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivLogIcon: ImageView = view.findViewById(R.id.ivLogIcon)
        val tvLogType: TextView = view.findViewById(R.id.tvLogType)
        val tvLogTimestamp: TextView = view.findViewById(R.id.tvLogTimestamp)
        val tvLogNote: TextView = view.findViewById(R.id.tvLogNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_care_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]
        
        holder.tvLogType.text = log.type.lowercase().replaceFirstChar { it.uppercase() }
        holder.tvLogNote.text = log.note
        
        try {
            val dateTime = LocalDateTime.parse(log.timestamp.split(".")[0])
            val now = LocalDateTime.now()
            
            if (dateTime.toLocalDate().isEqual(now.toLocalDate())) {
                val timeFormatter = DateTimeFormatter.ofPattern("'TODAY, 'h:mm a", Locale.ENGLISH)
                holder.tvLogTimestamp.text = dateTime.format(timeFormatter)
            } else {
                val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)
                holder.tvLogTimestamp.text = dateTime.format(dateFormatter)
            }
        } catch (e: Exception) {
            holder.tvLogTimestamp.text = log.timestamp
        }

        val context = holder.itemView.context
        val (iconRes, tintColor, bgRes) = when (log.type.uppercase()) {
            "WATERING" -> Triple(R.drawable.ic_water_drop, ContextCompat.getColor(context, R.color.halaman_green), R.drawable.bg_warning_light)
            "FERTILIZE" -> Triple(R.drawable.ic_add, ContextCompat.getColor(context, R.color.halaman_green), R.drawable.bg_circle_white_stroke)
            "PRUNING" -> Triple(R.drawable.ic_leaf, ContextCompat.getColor(context, R.color.halaman_green), R.drawable.bg_circle_white_stroke)
            else -> Triple(R.drawable.ic_check, ContextCompat.getColor(context, R.color.halaman_green), R.drawable.bg_circle_white_stroke)
        }
        
        holder.ivLogIcon.setImageResource(iconRes)
        holder.ivLogIcon.setColorFilter(tintColor)
        holder.ivLogIcon.setBackgroundResource(bgRes)
    }

    override fun getItemCount() = logs.size

    fun updateData(newLogs: List<CareLog>) {
        this.logs = newLogs
        notifyDataSetChanged()
    }
}
