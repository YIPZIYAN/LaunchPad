package com.example.launchpad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.launchpad.R
import com.example.launchpad.auth.viewmodel.LoginViewModel

class MainActivity : AppCompatActivity() {
    private val vm: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main) //change accordingly for testing
        vm.init()
    }
}