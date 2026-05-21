package edu.cit.devibar.halaman.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.cit.devibar.halaman.R
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarAdapter(
    private var days: List<LocalDate>,
    private val onDateSelected: (LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedDate: LocalDate = LocalDate.now()
    private var datesWithTasks: Set<LocalDate> = emptySet()

    class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDayName: TextView = view.findViewById(R.id.tvDayName)
        val tvDayNumber: TextView = view.findViewById(R.id.tvDayNumber)
        val vTaskIndicator: View = view.findViewById(R.id.vTaskIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        val today = LocalDate.now()
        
        holder.tvDayName.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).uppercase()
        holder.tvDayNumber.text = date.dayOfMonth.toString()

        // Highlight Today in Green
        if (date.isEqual(today)) {
            holder.tvDayNumber.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_circle_green)
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.tvDayNumber.elevation = 8f
        } else if (date.isEqual(selectedDate)) {
            // Highlight Selected in a light grey or stroke if not today
            holder.tvDayNumber.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.bg_circle_date)
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.halaman_green))
            holder.tvDayNumber.elevation = 2f
        } else {
            holder.tvDayNumber.background = null
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_dark))
            holder.tvDayNumber.elevation = 0f
        }

        // Orange task indicator
        holder.vTaskIndicator.visibility = if (datesWithTasks.contains(date)) View.VISIBLE else View.INVISIBLE

        holder.itemView.setOnClickListener {
            updateSelectedDate(date)
            onDateSelected(date)
        }
    }

    override fun getItemCount() = days.size

    fun updateDays(newDays: List<LocalDate>) {
        this.days = newDays
        notifyDataSetChanged()
    }

    fun updateSelectedDate(date: LocalDate) {
        val oldSelected = selectedDate
        selectedDate = date
        // Refresh only affected items if possible, but for simplicity notifyDataSetChanged
        notifyDataSetChanged()
    }

    fun updateDatesWithTasks(dates: Set<LocalDate>) {
        this.datesWithTasks = dates
        notifyDataSetChanged()
    }
}
