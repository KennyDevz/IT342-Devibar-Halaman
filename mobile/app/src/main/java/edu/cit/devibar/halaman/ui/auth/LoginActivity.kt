package edu.cit.devibar.halaman.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    // Views
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnGoogle: android.widget.LinearLayout
    private lateinit var tvSignUp: TextView
    private lateinit var tvForgotPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Init ViewModel
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Bind views
        tilEmail        = findViewById(R.id.tilEmail)
        tilPassword     = findViewById(R.id.tilPassword)
        etEmail         = findViewById(R.id.etEmail)
        etPassword      = findViewById(R.id.etPassword)
        btnLogin        = findViewById(R.id.btnLogin)
        btnGoogle       = findViewById(R.id.btnGoogle)
        tvSignUp        = findViewById(R.id.tvSignUp)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        // Show success toast if coming from register
        val successMessage = intent.getStringExtra("success_message")
        if (successMessage != null) {
            android.os.Handler(mainLooper).postDelayed({
                showSuccess(successMessage)
            }, 300)
        }

        // Login button
        btnLogin.setOnClickListener {
            handleLogin()
        }

        // Navigate to Register
        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Observe login result
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.success) {
                    // Save tokens
                    val prefs = getSharedPreferences("halaman_prefs", MODE_PRIVATE)
                    prefs.edit()
                        .putString("access_token", response.data?.accessToken)
                        .putString("refresh_token", response.data?.refreshToken)
                        .putString("user_first_name", response.data?.user?.firstName)
                        .putString("user_last_name", response.data?.user?.lastName)
                        .putString("user_email", response.data?.user?.email)
                        .putString("user_role", response.data?.user?.role)
                        .apply()

                    // Navigate to Dashboard
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("success_message", "Login successful! Welcome back 🌿")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    showError(response.error?.message ?: "Login failed")
                }
            }
            result.onFailure {
                showError("Invalid credentials. Please try again.")
            }
        }

        // Observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                btnLogin.isEnabled = false
                btnLogin.alpha = 0.7f
                btnLogin.text = "LOGGING IN..."
            } else {
                btnLogin.isEnabled = true
                btnLogin.alpha = 1.0f
                btnLogin.text = "LOG IN"
            }
        }
    }

    private fun handleLogin() {
        // Clear previous errors
        tilEmail.error = null
        tilPassword.error = null

        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate
        var isValid = true

        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Invalid email format"
            isValid = false
        }

        if (password.isEmpty()) {
            tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 8) {
            tilPassword.error = "Password must be at least 8 characters"
            isValid = false
        }

        if (!isValid) return

        // Call ViewModel
        viewModel.login(email, password)
    }

    override fun onResume() {
        super.onResume()
        tilEmail.error = null
        tilPassword.error = null
        etEmail.text?.clear()
        etPassword.text?.clear()
    }


    private fun showSuccess(message: String) {
        ToastHelper.showSuccess(this, message)
    }

    private fun showError(message: String) {
        ToastHelper.showError(this, message)
    }

}