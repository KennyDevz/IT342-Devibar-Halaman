package edu.cit.devibar.halaman.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import edu.cit.devibar.halaman.R

object ToastHelper {

    fun showSuccess(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.tvToastMessage).text = message
        layout.findViewById<TextView>(R.id.tvToastIcon).text = "✅"

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 80)
        toast.show()
    }

    fun showError(context: Context, message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)
        layout.findViewById<TextView>(R.id.tvToastMessage).text = message
        layout.findViewById<TextView>(R.id.tvToastIcon).text = "❌"
        layout.setBackgroundResource(R.drawable.bg_error_banner)
        layout.findViewById<TextView>(R.id.tvToastMessage)
            .setTextColor(context.getColor(R.color.error))

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG
        toast.view = layout
        toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 80)
        toast.show()
    }
}