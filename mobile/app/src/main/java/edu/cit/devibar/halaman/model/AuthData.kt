package edu.cit.devibar.halaman.model

data class AuthData(
    val user: UserDto?,
    val accessToken: String?,
    val refreshToken: String?
)