package edu.cit.devibar.halaman.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.model.Plant

class PlantAdapter(private var plants: List<Plant>) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPlantImage: ImageView = view.findViewById(R.id.ivPlantImage)
        val tvNickname: TextView = view.findViewById(R.id.tvPlantNickname)
        val tvSpecies: TextView = view.findViewById(R.id.tvPlantSpecies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plant_card, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plants[position]
        holder.tvNickname.text = plant.nickname
        holder.tvSpecies.text = plant.speciesName
        
        holder.ivPlantImage.load(plant.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.bg_glass_icon)
            error(R.drawable.bg_glass_icon)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, PlantDetailsActivity::class.java).apply {
                putExtra("PLANT_ID", plant.id)
                putExtra("NICKNAME", plant.nickname)
                putExtra("SPECIES", plant.speciesName)
                putExtra("IMAGE_URL", plant.imageUrl)
                putExtra("NEXT_DUE_DATE", plant.nextDueDate)
                putExtra("CREATED_AT", plant.createdAt)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = plants.size

    fun updateData(newPlants: List<Plant>) {
        this.plants = newPlants
        notifyDataSetChanged()
    }
}