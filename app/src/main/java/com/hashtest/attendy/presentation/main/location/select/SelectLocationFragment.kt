package com.hashtest.attendy.presentation.main.location.select

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.safeNavigate
import com.hashtest.attendy.databinding.FragmentSelectLocationBinding
import com.hashtest.attendy.presentation.base.BaseFragment

class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding, SelectLocationViewModel>(
    FragmentSelectLocationBinding::inflate
) {
    override val viewModel: SelectLocationViewModel by viewModels()

    override fun initView() {
        binding.apply {
            fabCreateNew.setOnClickListener{
                findNavController().safeNavigate(
                    SelectLocationFragmentDirections.actionSelectLocationFragmentToCreateLocationFragment()
                )
            }
        }
    }
}