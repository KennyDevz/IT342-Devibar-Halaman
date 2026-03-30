package edu.cit.devibar.halaman.api

import edu.cit.devibar.halaman.model.AuthResponse
import edu.cit.devibar.halaman.model.LoginRequest
import edu.cit.devibar.halaman.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}