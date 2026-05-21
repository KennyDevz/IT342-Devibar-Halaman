package edu.cit.devibar.halaman.model

data class BaseResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: ErrorResponse?,
    val timestamp: String?
)