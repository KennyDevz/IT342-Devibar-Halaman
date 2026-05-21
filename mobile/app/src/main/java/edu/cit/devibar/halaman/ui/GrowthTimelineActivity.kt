package edu.cit.devibar.halaman.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.CareLog
import edu.cit.devibar.halaman.utils.ToastHelper
import kotlinx.coroutines.launch

class GrowthTimelineActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.instance
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var rvTimeline: RecyclerView
    private lateinit var tvPlantJourney: TextView
    private lateinit var fabUpload: FloatingActionButton
    private lateinit var btnSort: Button

    private var allPhotos: List<CareLog> = emptyList()
    private var isNewestFirst: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_growth_timeline)

        val plantId = intent.getStringExtra("PLANT_ID") ?: return
        val plantName = intent.getStringExtra("PLANT_NAME") ?: "Plant"

        tvPlantJourney = findViewById(R.id.tvPlantJourney)
        tvPlantJourney.text = getString(R.string.plant_journey_format, plantName)

        rvTimeline = findViewById(R.id.rvTimeline)
        timelineAdapter = TimelineAdapter(emptyList())
        rvTimeline.layoutManager = LinearLayoutManager(this)
        rvTimeline.adapter = timelineAdapter

        btnSort = findViewById(R.id.btnSort)
        fabUpload = findViewById(R.id.fabCamera)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        
        btnSort.setOnClickListener { showSortMenu(it) }

        fabUpload.setOnClickListener {
            val dialog = NewGrowthEntryDialogFragment.newInstance(plantId, plantName) {
                fetchPlantImages(plantId)
            }
            dialog.show(supportFragmentManager, "NewGrowthEntry")
        }

        fetchPlantImages(plantId)
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menu.add(0, 1, 0, "Newest")
        popup.menu.add(0, 2, 1, "Oldest")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    isNewestFirst = true
                    btnSort.text = "SORT: NEWEST"
                    applySort()
                    true
                }
                2 -> {
                    isNewestFirst = false
                    btnSort.text = "SORT: OLDEST"
                    applySort()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun applySort() {
        val sorted = if (isNewestFirst) {
            allPhotos.sortedByDescending { it.timestamp }
        } else {
            allPhotos.sortedBy { it.timestamp }
        }
        timelineAdapter.updateData(sorted)
    }

    private fun fetchPlantImages(plantId: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.getPlantImages(plantId)
                if (response.isSuccessful && response.body() != null) {
                    val photos = response.body()!!
                    
                    allPhotos = photos.map { photo ->
                        CareLog(
                            id = photo.imageUrl,
                            type = "GROWTH",
                            note = photo.caption ?: "",
                            timestamp = photo.timestamp
                        )
                    }
                    applySort()
                }
            } catch (e: Exception) {
                ToastHelper.showError(this@GrowthTimelineActivity, "Failed to load timeline")
            }
        }
    }
}
