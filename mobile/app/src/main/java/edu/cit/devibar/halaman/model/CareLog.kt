package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class CareLog(
    @SerializedName("maintenanceId")
    val id: String,
    
    @SerializedName("taskType")
    val type: String,
    
    @SerializedName("notes")
    val note: String?,
    
    @SerializedName("completedAt")
    val timestamp: String
)

data class CareLogListResponse(
    val success: Boolean,
    val data: List<CareLog>?,
    val error: ErrorResponse?
)

data class CareLogSingleResponse(
    val success: Boolean,
    val data: CareLog?,
    val error: ErrorResponse?
)

data class CareLogRequest(
    val plantId: String,
    val taskType: String,
    val notes: String?
)
