package edu.cit.devibar.halaman.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.Plant
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class PlantsViewModel : ViewModel() {

    private val apiService = RetrofitClient.instance

    private val _plants = MutableLiveData<Result<List<Plant>>>()
    val plants: LiveData<Result<List<Plant>>> get() = _plants

    private val _addPlantResult = MutableLiveData<Result<Plant>?>()
    val addPlantResult: LiveData<Result<Plant>?> get() = _addPlantResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun fetchPlants() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getPlants()
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val plantsList = response.body()!!.data?.plants ?: emptyList()
                    _plants.value = Result.success(plantsList)
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to load plants: ${response.code()}"
                    _plants.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _plants.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPlant(context: Context, plant: Plant) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Call addPlant with JSON payload
                val response = apiService.addPlant(plant)
                if (response.isSuccessful && response.body() != null && response.body()!!.success) {
                    val createdPlant = response.body()!!.data!!.plant!!
                    
                    // If an image was selected, upload it
                    plant.imageUrl?.let { uriString ->
                        val uri = Uri.parse(uriString)
                        val file = getFileFromUri(context, uri)
                        if (file != null && createdPlant.id != null) {
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                            
                            try {
                                apiService.uploadPlantImage(createdPlant.id, imagePart)
                            } catch (e: Exception) {
                                e.printStackTrace() // Log image upload failure
                            }
                        }
                    }
                    
                    _addPlantResult.value = Result.success(createdPlant)
                    fetchPlants()
                } else {
                    val errorMsg = response.body()?.error?.message ?: "Failed to add plant: ${response.code()}"
                    _addPlantResult.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _addPlantResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetAddPlantResult() {
        _addPlantResult.value = null
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}