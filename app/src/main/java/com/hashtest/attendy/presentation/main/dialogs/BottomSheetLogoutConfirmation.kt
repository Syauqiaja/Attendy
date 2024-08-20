package com.hashtest.attendy.presentation.main.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.hashtest.attendy.databinding.BottomSheetLogoutConfirmationBinding

class BottomSheetLogoutConfirmation : BottomSheetDialogFragment(){
    private lateinit var binding: BottomSheetLogoutConfirmationBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLogoutConfirmationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }
            btnLogout.setOnClickListener {
                auth.signOut()
                dismiss()
            }
        }
    }
}