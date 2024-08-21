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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.hashtest.attendy.R
import com.hashtest.attendy.core.Resources
import com.hashtest.attendy.databinding.FragmentRegisterBinding
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.MainActivity
import timber.log.Timber

class RegisterFragment : BaseFragment<FragmentRegisterBinding, RegisterViewModel>(
    FragmentRegisterBinding::inflate
) {
    override val viewModel: RegisterViewModel by viewModels()

    override fun initView() {
        binding.apply {
            edtEmail.addTextChangedListener(textWatcherEmail)
            edtName.addTextChangedListener(edtName.defaultRequiredFieldTextWatcher())
            edtPassword.addTextChangedListener(textWatcherPassword)
            edtPasswordConfirm.addTextChangedListener(textWatcherConfirmPassword)

            btnRegister.setOnClickListener {
                if(validateForms()){
                    viewModel.createAuthUser(
                        name = edtName.text.toString(),
                        email = edtEmail.text.toString(),
                        password = edtPassword.text.toString()
                    ).observe(viewLifecycleOwner){response ->
                        when(response){
                            is Resources.Error -> {
                                showLoading(false)
                                requireContext().showCustomSnackBar("Failed to register user", root)
                            }
                            is Resources.Loading -> {
                                showLoading(true)
                            }
                            is Resources.Success -> {
                                showLoading(false)
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                requireActivity().startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                    }
                }
            }
            btnLogin.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun showLoading(toggle: Boolean){
        binding.apply {
            btnRegister.text = if(toggle) "" else getString(R.string.register)
            btnRegister.isEnabled = !toggle
            progressBar.visibility = if(toggle) View.VISIBLE else View.GONE
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