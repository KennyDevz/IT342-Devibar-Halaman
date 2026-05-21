package edu.cit.devibar.halaman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.GalleryViewModel

class GalleryFragment : Fragment() {

    private val viewModel: GalleryViewModel by viewModels()
    private lateinit var rvGallery: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var btnSort: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvGallery = view.findViewById(R.id.rvGallery)
        btnSort = view.findViewById(R.id.btnSort)
        
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        
        viewModel.fetchPhotos()
    }

    private fun setupRecyclerView() {
        galleryAdapter = GalleryAdapter(emptyList())
        rvGallery.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvGallery.adapter = galleryAdapter
    }

    private fun setupListeners() {
        btnSort.setOnClickListener { showSortMenu(it) }
    }

    private fun showSortMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 1, 0, "Newest")
        popup.menu.add(0, 2, 1, "Oldest")
        
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    viewModel.setSortOrder(true)
                    btnSort.text = "SORT: NEWEST"
                    true
                }
                2 -> {
                    viewModel.setSortOrder(false)
                    btnSort.text = "SORT: OLDEST"
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun observeViewModel() {
        viewModel.photos.observe(viewLifecycleOwner) { result ->
            result.onSuccess { photos ->
                galleryAdapter.updateData(photos)
            }
            result.onFailure { error ->
                ToastHelper.showError(requireContext(), "Error: ${error.message}")
            }
        }
    }
}
