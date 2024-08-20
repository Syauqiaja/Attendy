package com.hashtest.attendy.presentation.main.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentProfileBinding
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.dialogs.BottomSheetLogoutConfirmation

class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>(
    FragmentProfileBinding::inflate
) {
    override val viewModel: ProfileViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun initView() {
        binding.apply {
            tvName.text = auth.currentUser?.displayName
            tvEmail.text = auth.currentUser?.email

            btnLogout.setOnClickListener {
                val dialog = BottomSheetLogoutConfirmation()
                dialog.show(childFragmentManager, "Logout confirmation")
            }
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}