package edu.cit.devibar.halaman.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.utils.ToastHelper

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val successMessage = intent.getStringExtra("success_message")
        if (successMessage != null) {
            android.os.Handler(mainLooper).postDelayed({
                ToastHelper.showSuccess(this, successMessage)
            }, 300)
        }
    }
}