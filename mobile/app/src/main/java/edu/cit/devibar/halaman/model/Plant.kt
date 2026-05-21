package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class Plant(
    @SerializedName("plantId")
    val id: String? = null,
    
    @SerializedName("nickname")
    val nickname: String,
    
    @SerializedName("speciesName")
    val speciesName: String,
    
    @SerializedName("wateringFrequencyDays")
    val wateringFrequencyDays: Int,
    
    @SerializedName("imageUrl")
    val imageUrl: String? = null,

    @SerializedName("nextDueDate")
    val nextDueDate: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null
)