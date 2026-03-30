package edu.cit.devibar.halaman.model

data class AuthResponse(
    val success: Boolean,
    val data: AuthData?,
    val error: ErrorResponse?,
    val timestamp: String?
)