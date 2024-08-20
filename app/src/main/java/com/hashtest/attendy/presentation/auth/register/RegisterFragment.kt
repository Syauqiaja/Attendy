package com.hashtest.attendy.presentation.auth.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.safeNavigate
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentRegisterBinding
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.MainActivity

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>(
    FragmentRegisterBinding::inflate
) {
    override val viewModel: RegisterViewModel by viewModels()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun initView() {
        binding.apply {
            edtEmail.addTextChangedListener(textWatcherEmail)
            edtName.addTextChangedListener(edtName.defaultRequiredFieldTextWatcher())
            edtPassword.addTextChangedListener(textWatcherPassword)
            edtPasswordConfirm.addTextChangedListener(textWatcherConfirmPassword)

            btnRegister.setOnClickListener {
                if(validateForms()){
                    createAuthUser()
                }
            }
            btnLogin.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun createAuthUser() {
        auth.createUserWithEmailAndPassword(binding.edtEmail.text.toString(), binding.edtPassword.text.toString()).addOnCompleteListener {registerResult ->
            if(registerResult.isSuccessful){
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.edtName.text.toString()
                }
                auth.currentUser!!.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            requireActivity().startActivity(intent)
                            requireActivity().finish()
                        }else{
                            registerResult.result.user!!.delete()
                            auth.signOut()
                            requireContext().showCustomSnackBar(task.exception?.message ?: "Failed to register", binding.root)
                        }
                    }
            }else{
                requireContext().showCustomSnackBar(registerResult.exception?.message ?: "Failed to register", binding.root)
            }
        }
    }

    private fun validateForms(): Boolean {
        var result = true
        binding.apply {
            if(!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()){
                result = false
                edtEmail.error = getString(R.string.email_doesn_t_match_format)
            }
            if(binding.edtName.length() == 0){
                result = false
                binding.edtName.error = getString(R.string.please_fill_this_field)
            }
            if(binding.edtPassword.length() < 8){
                result = false
                binding.edtPassword.error = getString(R.string.minimum_password_is_8_characters)
            }
            if(!binding.edtPasswordConfirm.text.contentEquals(binding.edtPassword.text)){
                result = false
                binding.edtPasswordConfirm.error = getString(R.string.password_doesn_t_match)
            }
        }
        return result
    }

    private val textWatcherConfirmPassword = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            if(binding.edtPasswordConfirm.error != null){
                binding.edtPasswordConfirm.error = null
            }
        }
    }

    private val textWatcherPassword = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            if(binding.edtPassword.length() < 8 && binding.edtPassword.error != null){
                binding.edtPassword.error = getString(R.string.minimum_password_is_8_characters)
            }else{
                binding.edtPassword.error = null
            }
        }
    }

    private val textWatcherEmail = object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            if(binding.edtEmail.error != null){
                binding.edtEmail.error = null
            }
        }
    }

    private fun TextInputEditText.defaultRequiredFieldTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                if(this@defaultRequiredFieldTextWatcher.error != null){
                    this@defaultRequiredFieldTextWatcher.error = null
                }
            }

        }
    }
}