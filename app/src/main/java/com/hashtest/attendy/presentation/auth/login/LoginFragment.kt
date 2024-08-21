package com.hashtest.attendy.presentation.auth.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentLoginBinding
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.MainActivity
import timber.log.Timber

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>(
    FragmentLoginBinding::inflate
) {
    override val viewModel: LoginViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun initView() {
        binding.apply {
            edtEmail.addTextChangedListener(edtEmail.defaultRequiredFieldTextWatcher())
            edtPassword.addTextChangedListener(edtPassword.defaultRequiredFieldTextWatcher())

            btnLogin.setOnClickListener {
                if(validateFields()){
                    loginFirebase()
                }
            }
            btnRegister.setOnClickListener {
                findNavController().safeNavigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun validateFields(): Boolean {
        var result = true
        binding.apply {
            if(binding.edtEmail.length() == 0){
                binding.edtEmail.error = getString(R.string.please_fill_this_field)
                result = false
            }
            if(binding.edtPassword.length() == 0){
                binding.edtPassword.error = getString(R.string.please_fill_this_field)
                result = false
            }
        }
        return result
    }

    private fun loginFirebase() {
        binding.apply {
            showLoading(true)
            auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPassword.text.toString())
                .addOnCompleteListener { task ->
                    showLoading(false)
                    if(task.isSuccessful){
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finish()
                    }else{
                        requireContext().showCustomSnackBar(task.exception?.message ?: "Failed to login", root)
                    }
                }
        }
    }

    private fun showLoading(toggle: Boolean){
        binding.apply {
            btnLogin.text = if(toggle) "" else  getString(R.string.login)
            btnLogin.isEnabled = !toggle
            progressBar.visibility = if(toggle) View.VISIBLE else View.GONE
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