package com.example.cobacapstone.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.cobacapstone.R
import com.example.cobacapstone.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur warna status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // Menggunakan ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup navigasi
        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_panduan, R.id.navigation_prediksi, R.id.navigation_histori
            )
        )

        navView.setupWithNavController(navController)

        // Tambahkan setOnItemSelectedListener untuk mengatur navigasi manual
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.popBackStack(R.id.navigation_home, false) // Cegah duplikasi
                    true
                }
                R.id.navigation_panduan -> {
                    if (navController.currentDestination?.id != R.id.navigation_panduan) {
                        navController.navigate(R.id.navigation_panduan)
                    }
                    true
                }
                R.id.navigation_prediksi -> {
                    if (navController.currentDestination?.id != R.id.navigation_prediksi) {
                        navController.navigate(R.id.navigation_prediksi)
                    }
                    true
                }
                R.id.navigation_histori -> {
                    if (navController.currentDestination?.id != R.id.navigation_histori) {
                        navController.navigate(R.id.navigation_histori)
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun showSnackbar(message: String) {
        snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        snackbar?.show()
    }

    fun dismissSnackbar() {
        snackbar?.dismiss()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}