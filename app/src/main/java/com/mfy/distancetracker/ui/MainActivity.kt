package com.mfy.distancetracker.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mfy.distancetracker.R
import com.mfy.distancetracker.util.Permissions

class MainActivity : AppCompatActivity() {
    
    private lateinit var  navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {

        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        navController =navHostFragment.navController

        if(Permissions.hasLocationPermission(this)) {
            navController.navigate(R.id.action_permissionsFragment_to_mapsFragment)
        }
    }
}