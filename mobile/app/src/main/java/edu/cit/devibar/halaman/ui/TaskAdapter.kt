package edu.cit.devibar.halaman.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.CareTask
import edu.cit.devibar.halaman.model.CareTaskStatus

class TaskAdapter(
    private var tasks: List<CareTask>,
    private val onTaskClick: (CareTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlant: ImageView = view.findViewById(R.id.ivPlant)
        val tvTaskType: TextView = view.findViewById(R.id.tvTaskType)
        val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val ivAlert: ImageView = view.findViewById(R.id.ivAlert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_care_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        
        holder.tvPlantName.text = task.plantNickname
        holder.tvTaskType.text = task.type.name
        
        holder.tvStatus.text = when (task.status) {
            CareTaskStatus.DUE_TODAY -> "DUE TODAY"
            CareTaskStatus.OVERDUE -> "OVERDUE"
            else -> ""
        }

        holder.ivPlant.load(task.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.bg_glass_icon)
            error(R.drawable.bg_glass_icon)
            transformations(RoundedCornersTransformation(24f))
        }

        holder.itemView.setOnClickListener { onTaskClick(task) }
    }

    override fun getItemCount() = tasks.size

    fun updateData(newTasks: List<CareTask>) {
        this.tasks = newTasks
        notifyDataSetChanged()
    }
}
