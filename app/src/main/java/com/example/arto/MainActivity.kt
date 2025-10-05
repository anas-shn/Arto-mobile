package com.example.arto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.arto.data.local.SessionManager
import com.example.arto.databinding.ActivityMainBinding
import com.example.arto.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Log.d(TAG, "=== MainActivity onCreate ===")

        // Check authentication
        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            Log.d(TAG, "User not logged in, redirecting to LoginActivity")
            navigateToLogin()
            return
        }
        
        Log.d(TAG, "User logged in, showing main content")
        Log.d(TAG, "User ID: ${sessionManager.getUserId()}")
        Log.d(TAG, "User Name: ${sessionManager.getUserName()}")
        Log.d(TAG, "User Email: ${sessionManager.getUserEmail()}")

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
                    navController.popBackStack(R.id.navigation_home, false)
                    true
                }
                R.id.navigation_budget -> {
                    navController.popBackStack(R.id.navigation_home, false)
                    navController.navigate(R.id.navigation_budget)
                    true
                }
                R.id.navigation_transaction -> {
                    navController.popBackStack(R.id.navigation_home, false)
                    navController.navigate(R.id.navigation_transaction)
                    true
                }
                R.id.navigation_summary -> {
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
    
    private fun navigateToLogin() {
        Log.d(TAG, "=== Navigating to LoginActivity ===")
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}