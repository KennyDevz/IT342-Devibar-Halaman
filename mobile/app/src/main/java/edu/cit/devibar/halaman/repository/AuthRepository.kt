package edu.cit.devibar.halaman.repository

import edu.cit.devibar.halaman.api.RetrofitClient
import edu.cit.devibar.halaman.model.AuthResponse
import edu.cit.devibar.halaman.model.LoginRequest
import edu.cit.devibar.halaman.model.RegisterRequest

class AuthRepository {

    private val apiService = RetrofitClient.instance

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.register(
                RegisterRequest(firstName, lastName, email, password)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.login(
                LoginRequest(email, password)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}