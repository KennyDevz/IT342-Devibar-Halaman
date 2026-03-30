package edu.cit.devibar.halaman.model

data class ErrorResponse(
    val code: String?,
    val message: String?,
    val details: String?
)