package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class GalleryPhoto(
    @SerializedName("imageId")
    val id: String,
    
    @SerializedName("plantName")
    val plantName: String?,
    
    @SerializedName("fileUrl")
    val imageUrl: String,
    
    @SerializedName("uploadedAt")
    val dateAdded: String
)

data class GalleryResponse(
    val success: Boolean,
    val data: List<GalleryPhoto>?,
    val error: ErrorResponse?
)
