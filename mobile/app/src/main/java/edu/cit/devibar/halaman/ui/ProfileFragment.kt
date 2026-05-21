package edu.cit.devibar.halaman.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.cit.devibar.halaman.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvName = view.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = view.findViewById<TextView>(R.id.tvUserEmail)

        // Load user data from shared preferences
        val prefs = requireActivity().getSharedPreferences("halaman_prefs", Context.MODE_PRIVATE)
        val firstName = prefs.getString("user_first_name", "")
        val lastName = prefs.getString("user_last_name", "")
        val email = prefs.getString("user_email", "")

        tvName.text = if (firstName.isNullOrBlank()) "Gardener" else "$firstName $lastName"
        tvEmail.text = email

        view.findViewById<View>(R.id.llLogout).setOnClickListener {
            performLogout()
        }
    }

    private fun performLogout() {
        // 1. Clear SharedPreferences
        val prefs = requireActivity().getSharedPreferences("halaman_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        // 2. Clear Retrofit Auth Token
        edu.cit.devibar.halaman.api.RetrofitClient.authToken = null

        // 3. Navigate to LoginActivity and clear backstack
        val intent = android.content.Intent(requireContext(), edu.cit.devibar.halaman.ui.auth.LoginActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        requireActivity().finish()
    }
}
