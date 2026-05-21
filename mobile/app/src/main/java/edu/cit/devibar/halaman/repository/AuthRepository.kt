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

    suspend fun verifyOtp(
        email: String,
        otpCode: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.verifyOtp(
                edu.cit.devibar.halaman.model.VerifyOtpRequest(email, otpCode)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "OTP Verification failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resendOtp(
        email: String
    ): Result<AuthResponse> {
        return try {
            val response = apiService.resendOtp(
                edu.cit.devibar.halaman.model.ResendOtpRequest(email)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.error?.message ?: "Resend OTP failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
