package edu.cit.devibar.halaman.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var tvGreeting: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvHumidity: TextView
    private lateinit var tvCondition: TextView
    private lateinit var tvTasksTitle: TextView
    private lateinit var rvTasks: RecyclerView
    private lateinit var tvEmptyTasks: TextView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrevWeek: ImageView
    private lateinit var btnNextWeek: ImageView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvGreeting = view.findViewById(R.id.tvGreeting)
        tvTemperature = view.findViewById(R.id.tvTemperature)
        tvHumidity = view.findViewById(R.id.tvHumidity)
        tvCondition = view.findViewById(R.id.tvCondition)
        tvTasksTitle = view.findViewById(R.id.tvTasksTitle)
        rvTasks = view.findViewById(R.id.rvTasks)
        tvEmptyTasks = view.findViewById(R.id.tvEmptyTasks)
        rvCalendar = view.findViewById(R.id.rvCalendar)
        tvMonthYear = view.findViewById(R.id.tvMonthYear)
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek)
        btnNextWeek = view.findViewById(R.id.btnNextWeek)

        setupRecyclerView()
        setupCalendar()
        setupGreeting()
        observeViewModel()
        
        viewModel.fetchWeather()
        viewModel.fetchDailyTasks()
    }

    override fun onResume() {
        super.onResume()
        // Reset selection to today when returning to the fragment
        viewModel.resetToToday()
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val selectedDate = viewModel.selectedDate.value ?: LocalDate.now()
            if (selectedDate.isEqual(LocalDate.now())) {
                val dialog = AddCareLogDialogFragment.newInstance(task.plantId) {
                    viewModel.fetchDailyTasks()
                }
                dialog.show(parentFragmentManager, "AddCareLog")
            }
        }
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = taskAdapter
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter(emptyList()) { date ->
            viewModel.selectDate(date)
        }
        rvCalendar.layoutManager = GridLayoutManager(requireContext(), 7)
        rvCalendar.adapter = calendarAdapter

        btnPrevWeek.setOnClickListener { viewModel.previousWeek() }
        btnNextWeek.setOnClickListener { viewModel.nextWeek() }
    }

    private fun setupGreeting() {
        val prefs = requireActivity().getSharedPreferences("halaman_prefs", Context.MODE_PRIVATE)
        val firstName = prefs.getString("user_first_name", "Gardener")
        tvGreeting.text = getString(R.string.good_day_name, firstName)
    }

    private fun observeViewModel() {
        viewModel.weather.observe(viewLifecycleOwner) { result ->
            result.onSuccess { weather ->
                tvTemperature.text = getString(R.string.temperature_format, weather.temperature)
                tvHumidity.text = getString(R.string.humidity_format, weather.humidity)
                
                tvCondition.text = if (weather.isDay) {
                    getString(R.string.sunny_sky)
                } else {
                    getString(R.string.clear_night)
                }
            }
            result.onFailure { error ->
                ToastHelper.showError(requireContext(), getString(R.string.weather_error, error.message))
            }
        }

        viewModel.dailyTasks.observe(viewLifecycleOwner) { result ->
            result.onSuccess { tasks ->
                taskAdapter.updateData(tasks)
                if (tasks.isEmpty()) {
                    tvEmptyTasks.visibility = View.VISIBLE
                    rvTasks.visibility = View.GONE
                } else {
                    tvEmptyTasks.visibility = View.GONE
                    rvTasks.visibility = View.VISIBLE
                }
            }
            result.onFailure { error ->
                ToastHelper.showError(requireContext(), "Failed to load tasks: ${error.message}")
            }
        }

        viewModel.currentWeekStart.observe(viewLifecycleOwner) { startDay ->
            val weekDays = (0..6).map { startDay.plusDays(it.toLong()) }
            calendarAdapter.updateDays(weekDays)
            
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
            tvMonthYear.text = startDay.format(formatter)
            
            val actualStartOfThisWeek = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
            val isCurrentWeek = startDay.isEqual(actualStartOfThisWeek)
            
            btnPrevWeek.isEnabled = !isCurrentWeek
            btnPrevWeek.alpha = if (isCurrentWeek) 0.3f else 1.0f
        }

        viewModel.datesWithTasks.observe(viewLifecycleOwner) { dates ->
            calendarAdapter.updateDatesWithTasks(dates)
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            calendarAdapter.updateSelectedDate(date)
            
            // Update Title based on date
            val today = LocalDate.now()
            tvTasksTitle.text = when {
                date.isEqual(today) -> getString(R.string.tasks_for_today)
                date.isBefore(today) -> getString(R.string.past_tasks)
                else -> getString(R.string.upcoming_tasks)
            }
        }
    }
}
