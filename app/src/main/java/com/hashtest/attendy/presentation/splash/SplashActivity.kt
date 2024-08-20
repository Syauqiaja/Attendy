package com.hashtest.attendy.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.hashtest.attendy.R
import com.hashtest.attendy.presentation.auth.AuthActivity
import com.hashtest.attendy.presentation.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                true
            }
        }
        setContentView(R.layout.activity_splash)

        viewModel.isLoading.observe(this@SplashActivity){ isLoading ->
            if(!isLoading){
                val intent = if(auth.currentUser != null) { //Is user logged in
                    Intent(this@SplashActivity, MainActivity::class.java)
                }else{
                    Intent(this@SplashActivity, AuthActivity::class.java)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}