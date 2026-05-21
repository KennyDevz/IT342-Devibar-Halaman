package edu.cit.devibar.halaman.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper
import edu.cit.devibar.halaman.viewmodel.AuthViewModel

class VerifyOtpActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var email: String

    private lateinit var otpBoxes: List<EditText>
    private lateinit var btnVerify: androidx.appcompat.widget.AppCompatButton
    private lateinit var tvEmailHint: TextView
    private lateinit var tvResend: TextView

    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)

        email = intent.getStringExtra("EMAIL") ?: ""
        if (email.isEmpty()) {
            ToastHelper.showError(this, "Email not found")
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        btnVerify = findViewById(R.id.btnVerify)
        tvEmailHint = findViewById(R.id.tvEmailHint)
        tvResend = findViewById(R.id.tvResend)

        otpBoxes = listOf(
            findViewById(R.id.etOtp1),
            findViewById(R.id.etOtp2),
            findViewById(R.id.etOtp3),
            findViewById(R.id.etOtp4),
            findViewById(R.id.etOtp5),
            findViewById(R.id.etOtp6)
        )

        setupOtpBoxes()

        tvEmailHint.text = getString(R.string.enter_code_sent_to, email)

        btnVerify.setOnClickListener {
            val otpCode = otpBoxes.joinToString("") { it.text.toString() }
            if (otpCode.length < 6) {
                ToastHelper.showError(this, "Please enter the 6-digit code")
                return@setOnClickListener
            }
            viewModel.verifyOtp(email, otpCode)
        }

        tvResend.setOnClickListener {
            viewModel.resendOtp(email)
        }

        observeViewModel()
        startResendTimer()
    }

    private fun startResendTimer() {
        tvResend.isEnabled = false
        tvResend.alpha = 0.5f
        
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                tvResend.text = getString(R.string.resend_in, seconds)
            }

            override fun onFinish() {
                tvResend.text = getString(R.string.resend)
                tvResend.isEnabled = true
                tvResend.alpha = 1.0f
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun setupOtpBoxes() {
        for (i in otpBoxes.indices) {
            val currentBox = otpBoxes[i]
            
            currentBox.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (i < (otpBoxes.size - 1)) {
                            otpBoxes[i + 1].requestFocus()
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            currentBox.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (currentBox.text.isEmpty() && i > 0) {
                        otpBoxes[i - 1].requestFocus()
                        otpBoxes[i - 1].text.clear()
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.otpResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.success) {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("success_message", "Email verified successfully! Please log in.")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    ToastHelper.showError(this, response.error?.message ?: "Verification failed")
                }
            }
            result.onFailure {
                ToastHelper.showError(this, it.message ?: "Verification failed")
            }
        }

        viewModel.resendOtpResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.success) {
                    ToastHelper.showSuccess(this, "A new code has been sent to your email")
                    startResendTimer()
                } else {
                    ToastHelper.showError(this, response.error?.message ?: "Failed to resend code")
                }
            }
            result.onFailure {
                ToastHelper.showError(this, it.message ?: "Failed to resend code")
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            btnVerify.isEnabled = !isLoading
            btnVerify.text = if (isLoading) getString(R.string.verifying) else getString(R.string.verify)
        }
    }
}