package com.hashtest.attendy.presentation.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}