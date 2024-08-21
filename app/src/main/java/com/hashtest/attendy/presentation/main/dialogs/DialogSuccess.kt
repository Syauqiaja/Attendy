package com.hashtest.attendy.presentation.main.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.hashtest.attendy.databinding.DialogAttendanceSuccessBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DialogSuccess(
    private val title: String? = null,
    private val description: String? = null
):DialogFragment() {
    private lateinit var binding: DialogAttendanceSuccessBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAttendanceSuccessBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(title != null){
            binding.tvTitle.text = title
        }
        if(description != null){
            binding.tvDesc.text = description
        }else{
            binding.tvDesc.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            delay(1500)
            dismiss()
        }
    }
}