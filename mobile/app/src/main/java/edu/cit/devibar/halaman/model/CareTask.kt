package edu.cit.devibar.halaman.model

import com.google.gson.annotations.SerializedName

data class CareTask(
    val id: String,
    val plantId: String,
    val plantNickname: String,
    val speciesName: String,
    val imageUrl: String?,
    val type: CareTaskType,
    val status: CareTaskStatus,
    val dueDate: String
)

enum class CareTaskType {
    @SerializedName("WATERING") WATERING,
    @SerializedName("FERTILIZE") FERTILIZE,
    @SerializedName("PRUNING") PRUNING
}

enum class CareTaskStatus {
    @SerializedName("DUE_TODAY") DUE_TODAY,
    @SerializedName("OVERDUE") OVERDUE,
    @SerializedName("COMPLETED") COMPLETED
}

data class CareTaskResponse(
    val success: Boolean,
    val data: CareTaskData?,
    val error: ErrorResponse?
)

data class CareTaskData(
    val tasks: List<CareTask>
)
