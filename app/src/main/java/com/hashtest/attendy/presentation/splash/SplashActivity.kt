package com.hashtest.attendy.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hashtest.attendy.R
import com.hashtest.attendy.presentation.auth.AuthActivity

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
                setOnExitAnimationListener{
                    val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
        setContentView(R.layout.activity_splash)
    }
}