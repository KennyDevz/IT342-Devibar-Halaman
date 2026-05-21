package edu.cit.devibar.halaman.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.cit.devibar.halaman.R
import edu.cit.devibar.halaman.api.RetrofitClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ensure token is set if we came from a saved session
        val prefs = getSharedPreferences("halaman_prefs", MODE_PRIVATE)
        RetrofitClient.authToken = prefs.getString("access_token", null)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        
        bottomNavigation.setupWithNavController(navController)
    }
}