package edu.cit.devibar.halaman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.GalleryPhoto
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class GalleryViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    private val _photos = MutableLiveData<Result<List<GalleryPhoto>>>()
    val photos: LiveData<Result<List<GalleryPhoto>>> get() = _photos

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private var allPhotos: List<GalleryPhoto> = emptyList()
    private var isNewestFirst: Boolean = true

    fun fetchPhotos() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Reuse the existing plants endpoint
                val response = apiService.getPlants()
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val plants = response.body()!!.data?.plants ?: emptyList()
                    
                    // Manually flatten all images from all plants
                    // Since the current Plant model only has one imageUrl, we'll use that for now
                    // Ideally, the Plant model should have a list of images
                    allPhotos = plants.flatMap { plant ->
                        plant.imageUrl?.let { url ->
                            listOf(GalleryPhoto(
                                id = plant.id ?: "",
                                plantName = plant.nickname,
                                imageUrl = url,
                                dateAdded = formatDisplayDate(plant.createdAt)
                            ))
                        } ?: emptyList()
                    }
                    
                    applySort()
                } else {
                    _photos.value = Result.failure(Exception("Failed to fetch gallery"))
                }
            } catch (e: Exception) {
                _photos.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSortOrder(newestFirst: Boolean) {
        if (isNewestFirst != newestFirst) {
            isNewestFirst = newestFirst
            applySort()
        }
    }

    private fun applySort() {
        // Since we don't have a reliable sortable date yet, we'll just reverse the list
        val sorted = if (isNewestFirst) allPhotos else allPhotos.reversed()
        _photos.value = Result.success(sorted)
    }

    private fun formatDisplayDate(dateString: String?): String {
        if (dateString == null) return "N/A"
        return try {
            val dateTime = LocalDateTime.parse(dateString)
            dateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)).uppercase()
        } catch (e: Exception) {
            "N/A"
        }
    }
}
