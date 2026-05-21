package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class PlantImageResponse(
    @SerializedName("imageId")
    val id: String,
    
    @SerializedName("fileUrl")
    val imageUrl: String,
    
    @SerializedName("uploadedAt")
    val timestamp: String,
    
    val caption: String?
)
