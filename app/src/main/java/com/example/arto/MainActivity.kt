package com.example.arto

import android.os.Bundle
import androidx.core.view.WindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.arto.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Setup bottom navigation
        navView.setupWithNavController(navController)
        
        // Handle bottom nav item clicks properly
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Clear back stack and go to home
                    navController.popBackStack(R.id.navigation_home, false)
                    true
                }
                R.id.navigation_budget -> {
                    // Clear back stack and go to budget
                    navController.popBackStack(R.id.navigation_home, false)
                    navController.navigate(R.id.navigation_budget)
                    true
                }
                R.id.navigation_transaction -> {
                    // Clear back stack and go to transaction
                    navController.popBackStack(R.id.navigation_home, false)
                    navController.navigate(R.id.navigation_transaction)
                    true
                }
                R.id.navigation_summary -> {
                    // Clear back stack and go to summary
                    navController.popBackStack(R.id.navigation_home, false)
                    navController.navigate(R.id.navigation_summary)
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}