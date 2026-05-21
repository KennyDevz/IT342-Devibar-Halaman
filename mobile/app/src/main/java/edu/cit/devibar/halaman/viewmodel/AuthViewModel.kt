package edu.cit.devibar.halaman.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.devibar.halaman.model.AuthResponse
import edu.cit.devibar.halaman.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // --- Register State ---
    private val _registerResult = MutableLiveData<Result<AuthResponse>>()
    val registerResult: LiveData<Result<AuthResponse>> = _registerResult

    // --- Login State ---
    private val _loginResult = MutableLiveData<Result<AuthResponse>>()
    val loginResult: LiveData<Result<AuthResponse>> = _loginResult

    // --- OTP State ---
    private val _otpResult = MutableLiveData<Result<AuthResponse>>()
    val otpResult: LiveData<Result<AuthResponse>> = _otpResult

    private val _resendOtpResult = MutableLiveData<Result<AuthResponse>>()
    val resendOtpResult: LiveData<Result<AuthResponse>> = _resendOtpResult

    // --- Loading State ---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // --- Register ---
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.register(firstName, lastName, email, password)
            _registerResult.value = result
            _isLoading.value = false
        }
    }

    // --- Login ---
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.login(email, password)
            _loginResult.value = result
            _isLoading.value = false
        }
    }

    // --- OTP ---
    fun verifyOtp(email: String, otpCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.verifyOtp(email, otpCode)
            _otpResult.value = result
            _isLoading.value = false
        }
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.resendOtp(email)
            _resendOtpResult.value = result
            _isLoading.value = false
        }
    }
}
