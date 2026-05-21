package edu.cit.devibar.halaman.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.GalleryPhoto

class GalleryAdapter(private var photos: List<GalleryPhoto>) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: ImageView = view.findViewById(R.id.ivPhoto)
        val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val constraintLayout: ConstraintLayout = view.findViewById(R.id.clGalleryItemRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gallery_photo, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val photo = photos[position]
        
        holder.tvPlantName.text = photo.plantName ?: "Plant"
        holder.tvDate.text = photo.dateAdded

        val set = ConstraintSet()
        set.clone(holder.constraintLayout)
        val ratio = when (position % 4) {
            0 -> "H,1:1.4"
            1 -> "H,1:1"
            2 -> "H,1:1.2"
            else -> "H,1:1.6"
        }
        set.setDimensionRatio(R.id.ivPhoto, ratio)
        set.applyTo(holder.constraintLayout)

        holder.ivPhoto.load(photo.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.bg_glass_icon)
            error(R.drawable.bg_glass_icon)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, GrowthTimelineActivity::class.java).apply {
                putExtra("PLANT_ID", photo.id) // This is actually plant ID or Image ID? Based on ViewModel it was plant.id
                putExtra("PLANT_NAME", photo.plantName ?: "Plant")
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = photos.size

    fun updateData(newPhotos: List<GalleryPhoto>) {
        this.photos = newPhotos
        notifyDataSetChanged()
    }
}
