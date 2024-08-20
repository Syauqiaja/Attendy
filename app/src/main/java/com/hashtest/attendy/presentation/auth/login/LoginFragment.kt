package com.hashtest.attendy.presentation.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentLoginBinding
import com.hashtest.attendy.presentation.base.BaseFragment

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(
    FragmentLoginBinding::inflate
) {
    override val viewModel: LoginViewModel by viewModels()

    override fun initView() {
    }
}