package edu.cit.devibar.halaman.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    // Views
    private lateinit var tilFirstName: TextInputLayout
    private lateinit var tilLastName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilConfirmPassword: TextInputLayout
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnRegister: androidx.appcompat.widget.AppCompatButton
    private lateinit var tvTerms: TextView
    private lateinit var tvSignIn: TextView
    private lateinit var btnBack: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Init ViewModel
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Bind views
        tilFirstName        = findViewById(R.id.tilFirstName)
        tilLastName         = findViewById(R.id.tilLastName)
        tilEmail            = findViewById(R.id.tilEmail)
        tilPassword         = findViewById(R.id.tilPassword)
        tilConfirmPassword  = findViewById(R.id.tilConfirmPassword)
        etFirstName         = findViewById(R.id.etFirstName)
        etLastName          = findViewById(R.id.etLastName)
        etEmail             = findViewById(R.id.etEmail)
        etPassword          = findViewById(R.id.etPassword)
        etConfirmPassword   = findViewById(R.id.etConfirmPassword)
        btnRegister         = findViewById(R.id.btnRegister)
        tvTerms             = findViewById(R.id.tvTerms)
        tvSignIn            = findViewById(R.id.tvSignIn)
        btnBack             = findViewById(R.id.btnBack)

        // Set Terms of Service styled text
        setTermsText()

        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Navigate to Login
        tvSignIn.setOnClickListener {
            finish()
        }

        // Register button
        btnRegister.setOnClickListener {
            handleRegister()
        }

        // Observe register result
        viewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.success) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("success_message", "Account created! Please log in.")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    showError(response.error?.message ?: "Registration failed")
                }
            }
            result.onFailure {
                showError("Registration failed. Please try again.")
            }
        }

        // Observe loading
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                btnRegister.isEnabled = false
                btnRegister.alpha = 0.7f
                btnRegister.text = "CREATING ACCOUNT..."
            } else {
                btnRegister.isEnabled = true
                btnRegister.alpha = 1.0f
                btnRegister.text = "CREATE ACCOUNT"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tilFirstName.error      = null
        tilLastName.error       = null
        tilEmail.error          = null
        tilPassword.error       = null
        tilConfirmPassword.error = null
        etFirstName.text?.clear()
        etLastName.text?.clear()
        etEmail.text?.clear()
        etPassword.text?.clear()
        etConfirmPassword.text?.clear()
    }

    private fun handleRegister() {
        tilFirstName.error       = null
        tilLastName.error        = null
        tilEmail.error           = null
        tilPassword.error        = null
        tilConfirmPassword.error = null

        val firstName       = etFirstName.text.toString().trim()
        val lastName        = etLastName.text.toString().trim()
        val email           = etEmail.text.toString().trim()
        val password        = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Validate
        var isValid = true

        if (firstName.isEmpty()) {
            tilFirstName.error = "First name is required"
            isValid = false
        }

        if (lastName.isEmpty()) {
            tilLastName.error = "Last name is required"
            isValid = false
        }

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

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.error = "Please confirm your password"
            isValid = false
        } else if (password != confirmPassword) {
            tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        }

        if (!isValid) return

        // Call ViewModel
        viewModel.register(firstName, lastName, email, password)
    }

    private fun setTermsText() {
        val fullText = "By joining Halaman, you agree to our Terms of Service and Privacy Policy."
        val spannable = SpannableString(fullText)
        val green = ContextCompat.getColor(this, R.color.halaman_green)

        // Style "Terms of Service"
        val termsStart = fullText.indexOf("Terms of Service")
        val termsEnd   = termsStart + "Terms of Service".length
        spannable.setSpan(ForegroundColorSpan(green), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Style "Privacy Policy"
        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd   = privacyStart + "Privacy Policy".length
        spannable.setSpan(ForegroundColorSpan(green), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(UnderlineSpan(), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        tvTerms.text = spannable
        tvTerms.textSize = 10f
        tvTerms.gravity = android.view.Gravity.CENTER
        tvTerms.setTextColor(ContextCompat.getColor(this, R.color.text_secondary))
    }

    private fun showError(message: String) {
        ToastHelper.showError(this, message)
    }

}