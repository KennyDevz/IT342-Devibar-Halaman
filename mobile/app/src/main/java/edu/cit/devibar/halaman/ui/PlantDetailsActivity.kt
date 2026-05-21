package edu.cit.devibar.halaman.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.utils.ToastHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class PlantDetailsActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.instance
    private lateinit var careLogAdapter: CareLogAdapter
    
    private lateinit var ivPlantBanner: ImageView
    private lateinit var tvNickname: TextView
    private lateinit var tvSpecies: TextView
    private lateinit var tvNextWater: TextView
    private lateinit var tvAge: TextView
    private lateinit var rvCareHistory: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_details)

        val plantId = intent.getStringExtra("PLANT_ID") ?: return
        val nickname = intent.getStringExtra("NICKNAME")
        val species = intent.getStringExtra("SPECIES")
        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val nextDueDate = intent.getStringExtra("NEXT_DUE_DATE")
        val createdAt = intent.getStringExtra("CREATED_AT")

        initViews()
        setupListeners()
        
        tvNickname.text = nickname
        tvSpecies.text = species
        ivPlantBanner.load(imageUrl) {
            placeholder(R.drawable.bg_glass_icon)
            error(R.drawable.bg_glass_icon)
        }

        calculateNextWater(nextDueDate)
        calculateAge(createdAt)
        
        fetchCareHistory(plantId)
    }

    private fun calculateAge(createdAt: String?) {
        if (createdAt == null) {
            tvAge.text = "N/A"
            return
        }

        try {
            val datePart = createdAt.split("T")[0]
            val createdDate = LocalDate.parse(datePart)
            val today = LocalDate.now()
            
            val months = ChronoUnit.MONTHS.between(createdDate, today)
            val days = ChronoUnit.DAYS.between(createdDate, today)

            tvAge.text = when {
                months >= 12 -> {
                    val years = months / 12
                    val remMonths = months % 12
                    if (remMonths > 0) "$years Y, $remMonths M" else "$years Years"
                }
                months > 0 -> "$months Months"
                else -> "$days Days"
            }
        } catch (e: Exception) {
            tvAge.text = "N/A"
        }
    }

    private fun initViews() {
        ivPlantBanner = findViewById(R.id.ivPlantBanner)
        tvNickname = findViewById(R.id.tvNickname)
        tvSpecies = findViewById(R.id.tvSpecies)
        tvNextWater = findViewById(R.id.tvNextWater)
        tvAge = findViewById(R.id.tvAge)
        rvCareHistory = findViewById(R.id.rvCareHistory)

        careLogAdapter = CareLogAdapter(emptyList())
        rvCareHistory.layoutManager = LinearLayoutManager(this)
        rvCareHistory.adapter = careLogAdapter
    }

    private fun setupListeners() {
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        
        findViewById<View>(R.id.btnAddCareLog).setOnClickListener {
            val plantId = intent.getStringExtra("PLANT_ID") ?: return@setOnClickListener
            val dialog = AddCareLogDialogFragment.newInstance(plantId) {
                fetchCareHistory(plantId)
                // Optionally refresh next watering date if logic exists
            }
            dialog.show(supportFragmentManager, "AddCareLogDialog")
        }
    }

    private fun calculateNextWater(nextDueDate: String?) {
        if (nextDueDate == null) {
            tvNextWater.text = "N/A"
            return
        }

        try {
            val datePart = nextDueDate.split("T")[0]
            val dueDate = LocalDate.parse(datePart)
            val today = LocalDate.now()
            val days = ChronoUnit.DAYS.between(today, dueDate)
            
            tvNextWater.text = when {
                days < 0 -> "Overdue"
                days == 0L -> "Today"
                days == 1L -> "Tomorrow"
                else -> "$days Days"
            }
        } catch (e: Exception) {
            tvNextWater.text = "N/A"
        }
    }

    private fun fetchCareHistory(plantId: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.getCareLogs(plantId)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val logs = response.body()!!.data ?: emptyList()
                    careLogAdapter.updateData(logs)
                }
            } catch (e: Exception) {
                // Silently handle or show log
            }
        }
    }
}
