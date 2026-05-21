package edu.cit.devibar.halaman.ui

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.Plant
import edu.cit.devibar.halaman.viewmodel.PlantsViewModel

class AddPlantDialogFragment : BottomSheetDialogFragment() {

    private val viewModel: PlantsViewModel by activityViewModels()
    private var selectedImageUri: Uri? = null

    private lateinit var etNickname: EditText
    private lateinit var etSpecies: EditText
    private lateinit var etFrequency: EditText
    private lateinit var btnAdd: Button
    private lateinit var clPhotoSelector: ConstraintLayout
    private lateinit var ivSelectedPhoto: ImageView
    private lateinit var pbLoading: ProgressBar

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            ivSelectedPhoto.visibility = View.VISIBLE
            ivSelectedPhoto.load(it) {
                transformations(RoundedCornersTransformation(24f))
            }
            validateFields()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_add_plant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etNickname = view.findViewById(R.id.etNickname)
        etSpecies = view.findViewById(R.id.etSpecies)
        etFrequency = view.findViewById(R.id.etFrequency)
        btnAdd = view.findViewById(R.id.btnAddPlant)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        clPhotoSelector = view.findViewById(R.id.clPhotoSelector)
        pbLoading = view.findViewById(R.id.pbLoading)
        
        // Dynamically add an ImageView for photo preview if not in XML
        ivSelectedPhoto = ImageView(context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            visibility = View.GONE
        }
        clPhotoSelector.addView(ivSelectedPhoto)

        ivClose.setOnClickListener { dismiss() }
        
        clPhotoSelector.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFields()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etNickname.addTextChangedListener(textWatcher)
        etSpecies.addTextChangedListener(textWatcher)
        etFrequency.addTextChangedListener(textWatcher)

        observeViewModel()
        validateFields()

        btnAdd.setOnClickListener {
            val nickname = etNickname.text.toString().trim()
            val species = etSpecies.text.toString().trim()
            val frequencyStr = etFrequency.text.toString().trim()

            val frequency = frequencyStr.toIntOrNull() ?: 7
            val newPlant = Plant(
                nickname = nickname,
                speciesName = species,
                wateringFrequencyDays = frequency,
                imageUrl = selectedImageUri?.toString()
            )

            viewModel.addPlant(requireContext(), newPlant)
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            setLoadingState(isLoading)
        }

        viewModel.addPlantResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            
            if (result.isSuccess) {
                viewModel.resetAddPlantResult()
                dismiss()
            } else {
                result.exceptionOrNull()?.message?.let {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
                viewModel.resetAddPlantResult()
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnAdd.isEnabled = !isLoading && isFormValid()
        btnAdd.text = if (isLoading) "" else getString(R.string.add_plant_button)
        
        // Disable other inputs while loading
        etNickname.isEnabled = !isLoading
        etSpecies.isEnabled = !isLoading
        etFrequency.isEnabled = !isLoading
        clPhotoSelector.isEnabled = !isLoading
        
        btnAdd.alpha = if (isLoading || !isFormValid()) 0.5f else 1.0f
    }

    private fun validateFields() {
        val isValid = isFormValid()
        btnAdd.isEnabled = isValid
        btnAdd.alpha = if (isValid) 1.0f else 0.5f
    }

    private fun isFormValid(): Boolean {
        val nickname = etNickname.text.toString().trim()
        val species = etSpecies.text.toString().trim()
        val frequency = etFrequency.text.toString().trim()
        
        return nickname.isNotEmpty() && 
               species.isNotEmpty() && 
               frequency.isNotEmpty() && 
               selectedImageUri != null
    }
}