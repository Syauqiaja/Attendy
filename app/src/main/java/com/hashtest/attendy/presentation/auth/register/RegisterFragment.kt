package com.hashtest.attendy.presentation.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.safeNavigate
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentRegisterBinding
import com.hashtest.attendy.presentation.base.BaseFragment

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>(
    FragmentRegisterBinding::inflate
) {
    override val viewModel: RegisterViewModel by viewModels()

    override fun initView() {
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}