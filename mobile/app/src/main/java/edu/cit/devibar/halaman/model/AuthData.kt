package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class AuthData(
    @SerializedName("user")
    val user: UserDto? = null,
    
    @SerializedName("accessToken")
    val accessToken: String? = null,
    
    @SerializedName("refreshToken")
    val refreshToken: String? = null,
    
    @SerializedName("plant")
    val plant: Plant? = null,
    
    @SerializedName("plants")
    val plants: List<Plant>? = null
)