package edu.cit.devibar.halaman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.CareTask
import edu.cit.devibar.halaman.model.CareTaskStatus
import edu.cit.devibar.halaman.model.CareTaskType
import edu.cit.devibar.halaman.model.WeatherResponse
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    private val _weather = MutableLiveData<Result<WeatherResponse>>()
    val weather: LiveData<Result<WeatherResponse>> get() = _weather

    private val _dailyTasks = MutableLiveData<Result<List<CareTask>>>()
    val dailyTasks: LiveData<Result<List<CareTask>>> get() = _dailyTasks

    private val _datesWithTasks = MutableLiveData<Set<LocalDate>>()
    val datesWithTasks: LiveData<Set<LocalDate>> get() = _datesWithTasks

    private val _selectedDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val _currentWeekStart = MutableLiveData<LocalDate>(
        LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
    )
    val currentWeekStart: LiveData<LocalDate> get() = _currentWeekStart

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var allPlantsTasks: List<CareTask> = emptyList()

    fun fetchWeather() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getCurrentWeather()
                if (response.isSuccessful && response.body() != null) {
                    _weather.value = Result.success(response.body()!!)
                } else {
                    _weather.value = Result.failure(Exception("Failed to load weather: ${response.code()}"))
                }
            } catch (e: Exception) {
                _weather.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        filterTasksForDate(date)
    }

    fun resetToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        // Also ensure we are showing the week containing today
        _currentWeekStart.value = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
        filterTasksForDate(today)
    }

    fun fetchDailyTasks() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getPlants()
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val plants = response.body()!!.data?.plants ?: emptyList()
                    val today = LocalDate.now()
                    
                    val allTasks = plants.filter { it.nextDueDate != null }.mapNotNull { plant ->
                        try {
                            val datePart = plant.nextDueDate!!.split("T")[0]
                            val dueDate = LocalDate.parse(datePart)
                            
                            CareTask(
                                id = plant.id ?: "",
                                plantId = plant.id ?: "",
                                plantNickname = plant.nickname,
                                speciesName = plant.speciesName,
                                imageUrl = plant.imageUrl,
                                type = CareTaskType.WATERING,
                                status = if (dueDate.isBefore(today)) CareTaskStatus.OVERDUE else CareTaskStatus.DUE_TODAY,
                                dueDate = plant.nextDueDate
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    allPlantsTasks = allTasks
                    
                    // Identify which dates have tasks
                    val taskDates = allTasks.map { LocalDate.parse(it.dueDate.split("T")[0]) }.toSet()
                    _datesWithTasks.value = taskDates
                    
                    // Filter for selected date (default is today)
                    filterTasksForDate(_selectedDate.value ?: LocalDate.now())
                    
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load tasks: ${response.code()}"
                    _dailyTasks.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _dailyTasks.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterTasksForDate(date: LocalDate) {
        val today = LocalDate.now()
        val tasksForDate = allPlantsTasks.filter { 
            val taskDate = LocalDate.parse(it.dueDate.split("T")[0])
            if (date.isEqual(today)) {
                // For today, show today's tasks AND any overdue tasks from the past
                taskDate.isEqual(today) || taskDate.isBefore(today)
            } else {
                // For other dates, show only tasks specific to that date
                taskDate.isEqual(date)
            }
        }
        _dailyTasks.value = Result.success(tasksForDate)
    }

    fun nextWeek() {
        _currentWeekStart.value = _currentWeekStart.value?.plusWeeks(1)
    }

    fun previousWeek() {
        val currentStart = _currentWeekStart.value ?: return
        val actualStartOfThisWeek = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
        
        if (currentStart.isAfter(actualStartOfThisWeek)) {
            _currentWeekStart.value = currentStart.minusWeeks(1)
        }
    }
}
