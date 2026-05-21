package edu.cit.devibar.halaman.api

import edu.cit.devibar.halaman.model.AuthResponse
import edu.cit.devibar.halaman.model.CareLogListResponse
import edu.cit.devibar.halaman.model.CareLogSingleResponse
import edu.cit.devibar.halaman.model.LoginRequest
import edu.cit.devibar.halaman.model.PlantImageResponse
import edu.cit.devibar.halaman.model.Plant
import edu.cit.devibar.halaman.model.RegisterRequest
import edu.cit.devibar.halaman.model.WeatherResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: edu.cit.devibar.halaman.model.VerifyOtpRequest
    ): Response<AuthResponse>

    @POST("api/auth/resend-otp")
    suspend fun resendOtp(
        @Body request: edu.cit.devibar.halaman.model.ResendOtpRequest
    ): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @GET("api/weather/current")
    suspend fun getCurrentWeather(): Response<WeatherResponse>

    @GET("api/plants")
    suspend fun getPlants(): Response<AuthResponse>

    @POST("api/plants")
    suspend fun addPlant(
        @Body plant: Plant
    ): Response<AuthResponse>

    @Multipart
    @POST("api/plants/{id}/images")
    suspend fun uploadPlantImage(
        @retrofit2.http.Path("id") id: String,
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody? = null
    ): Response<AuthResponse>

    @GET("api/plants/{id}/images/history")
    suspend fun getPlantImages(
        @retrofit2.http.Path("id") id: String
    ): Response<List<PlantImageResponse>>

    @GET("api/maintenance/{id}")
    suspend fun getCareLogs(
        @retrofit2.http.Path("id") id: String
    ): Response<CareLogListResponse>

    @POST("api/maintenance")
    suspend fun addCareLog(
        @Body request: edu.cit.devibar.halaman.model.CareLogRequest
    ): Response<CareLogSingleResponse>
}