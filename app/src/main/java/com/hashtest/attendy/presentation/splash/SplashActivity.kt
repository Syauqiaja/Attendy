package com.hashtest.attendy.presentation.splash

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hashtest.attendy.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            viewModel.isLoading.observe(this@SplashActivity){ isLoading ->
                setKeepOnScreenCondition(){
                    return@setKeepOnScreenCondition !isLoading
                }
            }
        }
        setContentView(R.layout.activity_splash)
    }
}