package edu.cit.devibar.halaman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.PlantsViewModel

class PlantsFragment : Fragment() {

    private val viewModel: PlantsViewModel by activityViewModels()

    private lateinit var rvPlants: RecyclerView
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var tvPlantCount: TextView
    private lateinit var etSearch: EditText
    private lateinit var btnAddPlant: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPlantCount = view.findViewById(R.id.tvPlantCount)
        etSearch = view.findViewById(R.id.etSearch)
        rvPlants = view.findViewById(R.id.rvPlants)
        btnAddPlant = view.findViewById(R.id.btnAddPlant)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        viewModel.fetchPlants()
    }

    private fun setupRecyclerView() {
        plantAdapter = PlantAdapter(emptyList())
        rvPlants.layoutManager = GridLayoutManager(requireContext(), 2)
        rvPlants.adapter = plantAdapter
    }

    private fun setupListeners() {
        btnAddPlant.setOnClickListener {
            val dialog = AddPlantDialogFragment()
            dialog.show(parentFragmentManager, "AddPlantDialog")
        }
    }

    private fun observeViewModel() {
        viewModel.plants.observe(viewLifecycleOwner) { result ->
            result.onSuccess { plants ->
                plantAdapter.updateData(plants)
                tvPlantCount.text = getString(R.string.plants_in_total, plants.size)
            }
            result.onFailure { error ->
                ToastHelper.showError(requireContext(), "Error: ${error.message}")
            }
        }

        viewModel.addPlantResult.observe(viewLifecycleOwner) { result ->
            result?.onSuccess {
                ToastHelper.showSuccess(requireContext(), "Plant added successfully!")
            }
            result?.onFailure { error ->
                ToastHelper.showError(requireContext(), "Failed to add plant: ${error.message}")
            }
        }
    }
}