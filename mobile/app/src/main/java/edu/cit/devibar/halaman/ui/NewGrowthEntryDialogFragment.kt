package edu.cit.devibar.halaman.ui

import android.net.Uri
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.utils.ToastHelper
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class NewGrowthEntryDialogFragment : BottomSheetDialogFragment() {

    private val apiService = RetrofitClient.instance
    private lateinit var plantId: String
    private lateinit var plantName: String
    private var onEntryAdded: (() -> Unit)? = null
    private var selectedImageUri: Uri? = null

    private lateinit var ivSelectedPhoto: ImageView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var btnSave: Button
    private lateinit var pbLoading: ProgressBar

    companion object {
        fun newInstance(plantId: String, plantName: String, onEntryAdded: () -> Unit): NewGrowthEntryDialogFragment {
            return NewGrowthEntryDialogFragment().apply {
                this.plantId = plantId
                this.plantName = plantName
                this.onEntryAdded = onEntryAdded
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivSelectedPhoto.visibility = View.VISIBLE
            llEmptyState.visibility = View.GONE
            ivSelectedPhoto.load(it)
            validateForm()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_new_growth_entry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvPlantName = view.findViewById<TextView>(R.id.tvPlantName)
        val etNote = view.findViewById<EditText>(R.id.etNote)
        val clPhotoSelector = view.findViewById<ConstraintLayout>(R.id.clPhotoSelector)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        
        ivSelectedPhoto = view.findViewById(R.id.ivSelectedPhoto)
        llEmptyState = view.findViewById(R.id.llEmptyState)
        btnSave = view.findViewById(R.id.btnSave)
        pbLoading = view.findViewById(R.id.pbLoading)

        tvPlantName.text = plantName
        ivClose.setOnClickListener { dismiss() }
        
        clPhotoSelector.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnSave.setOnClickListener {
            saveEntry(etNote.text.toString())
        }

        validateForm()
    }

    private fun validateForm() {
        val isValid = selectedImageUri != null
        btnSave.isEnabled = isValid
        btnSave.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun saveEntry(note: String) {
        val uri = selectedImageUri ?: return
        
        lifecycleScope.launch {
            try {
                setLoading(true)
                
                val file = getFileFromUri(uri)
                if (file == null) {
                    ToastHelper.showError(requireContext(), "Failed to process image")
                    setLoading(false)
                    return@launch
                }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                val captionPart = if (note.isNotBlank()) {
                    note.toRequestBody("text/plain".toMediaTypeOrNull())
                } else null

                val response = apiService.uploadPlantImage(plantId, imagePart, captionPart)
                if (response.isSuccessful) {
                    onEntryAdded?.invoke()
                    dismiss()
                } else {
                    ToastHelper.showError(requireContext(), "Failed to upload: ${response.code()}")
                    setLoading(false)
                }

            } catch (e: Exception) {
                ToastHelper.showError(requireContext(), "Error: ${e.message}")
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnSave.isEnabled = !isLoading
        btnSave.text = if (isLoading) "" else "ADD TO JOURNEY"
        pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun getFileFromUri(uri: Uri): File? {
        val context = requireContext()
        val file = File(context.cacheDir, "growth_${System.currentTimeMillis()}.jpg")
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
