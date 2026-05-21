package edu.cit.devibar.halaman.model

data class VerifyOtpRequest(
    val email: String,
    val otpCode: String
)