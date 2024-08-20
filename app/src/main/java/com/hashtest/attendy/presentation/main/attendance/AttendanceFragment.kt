package com.hashtest.attendy.presentation.main.attendance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentAttendanceBinding
import com.hashtest.attendy.presentation.base.BaseFragment
import com.ncorti.slidetoact.SlideToActView

class AttendanceFragment : BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>(
    FragmentAttendanceBinding::inflate
) {
    override val viewModel: AttendanceViewModel by viewModels()

    override fun initView() {
        binding.apply {
            slideButton.onSlideCompleteListener = onCompleteSlide
        }
    }

    private val onCompleteSlide: SlideToActView.OnSlideCompleteListener = object :SlideToActView.OnSlideCompleteListener{
        override fun onSlideComplete(view: SlideToActView) {
            findNavController().popBackStack()
        }
    }
}