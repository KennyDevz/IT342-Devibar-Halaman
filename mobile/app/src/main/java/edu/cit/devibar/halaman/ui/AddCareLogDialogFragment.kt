package edu.cit.devibar.halaman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.CareLogRequest
import edu.cit.devibar.halaman.utils.ToastHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class AddCareLogDialogFragment : BottomSheetDialogFragment() {

    private val apiService = RetrofitClient.instance
    private var selectedType: String = "WATERING"
    private lateinit var plantId: String
    private var onLogAdded: (() -> Unit)? = null

    companion object {
        fun newInstance(plantId: String, onLogAdded: () -> Unit): AddCareLogDialogFragment {
            return AddCareLogDialogFragment().apply {
                this.plantId = plantId
                this.onLogAdded = onLogAdded
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_add_care_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCurrentDate = view.findViewById<TextView>(R.id.tvCurrentDate)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSaveLog)
        val pbLoading = view.findViewById<ProgressBar>(R.id.pbLoading)

        val llWatering = view.findViewById<LinearLayout>(R.id.llWatering)
        val llFertilize = view.findViewById<LinearLayout>(R.id.llFertilize)
        val llMist = view.findViewById<LinearLayout>(R.id.llMist)
        val llPruning = view.findViewById<LinearLayout>(R.id.llPruning)

        val typeViews = listOf(llWatering, llFertilize, llMist, llPruning)

        tvCurrentDate.text = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH))

        llWatering.setOnClickListener { selectType("WATERING", typeViews) }
        llFertilize.setOnClickListener { selectType("FERTILIZE", typeViews) }
        llMist.setOnClickListener { selectType("MIST", typeViews) }
        llPruning.setOnClickListener { selectType("PRUNING", typeViews) }

        btnSave.setOnClickListener {
            saveLog(etNotes.text.toString(), btnSave, pbLoading)
        }
    }

    private fun selectType(type: String, views: List<LinearLayout>) {
        selectedType = type
        views.forEach { view ->
            val isSelected = when (view.id) {
                R.id.llWatering -> type == "WATERING"
                R.id.llFertilize -> type == "FERTILIZE"
                R.id.llMist -> type == "MIST"
                R.id.llPruning -> type == "PRUNING"
                else -> false
            }
            
            view.setBackgroundResource(if (isSelected) R.drawable.bg_task_type_selected else R.drawable.bg_task_type_unselected)
            
            val icon = view.getChildAt(0) as ImageView
            val text = view.getChildAt(1) as TextView
            val color = ContextCompat.getColor(requireContext(), if (isSelected) R.color.halaman_green else R.color.gray_light)
            
            icon.setColorFilter(color)
            text.setTextColor(color)
        }
    }

    private fun saveLog(note: String, button: Button, progress: ProgressBar) {
        lifecycleScope.launch {
            try {
                button.isEnabled = false
                button.text = ""
                progress.visibility = View.VISIBLE

                val request = CareLogRequest(
                    plantId = plantId,
                    taskType = selectedType,
                    notes = if (note.isBlank()) null else note
                )

                val response = apiService.addCareLog(request)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    onLogAdded?.invoke()
                    dismiss()
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to save log"
                    ToastHelper.showError(requireContext(), errorMsg)
                    button.isEnabled = true
                    button.text = "SAVE LOG"
                    progress.visibility = View.GONE
                }
                
            } catch (e: Exception) {
                button.isEnabled = true
                button.text = "SAVE LOG"
                progress.visibility = View.GONE
                ToastHelper.showError(requireContext(), "Error connecting to server")
            }
        }
    }
}
