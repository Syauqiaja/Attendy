package com.hashtest.attendy.presentation.main.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentSelectLocationBinding
import com.hashtest.attendy.presentation.base.BaseFragment

class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding, SelectLocationViewModel>(
    FragmentSelectLocationBinding::inflate
) {
    override val viewModel: SelectLocationViewModel by viewModels()

    override fun initView() {

    }
}