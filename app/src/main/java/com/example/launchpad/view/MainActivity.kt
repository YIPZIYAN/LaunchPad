package com.example.launchpad.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.launchpad.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main) //change accordingly for testing
        /*
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, ApplicantDetailsFragment()) // Replace 'R.id.fragment_container' with the id of the container in your activity's layout
        transaction.addToBackStack(null) // Optional, adds this transaction to the back stack
        transaction.commit()

         */
    }
}