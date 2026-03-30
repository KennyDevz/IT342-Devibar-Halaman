package edu.cit.devibar.halaman.model

data class UserDto(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)