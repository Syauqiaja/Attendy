package com.hashtest.attendy.presentation.main.location.select

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aglotest.algolist.utils.safeNavigate
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.databinding.FragmentSelectLocationBinding
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.presentation.adapters.CheckableLocationAdapter
import com.hashtest.attendy.presentation.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding, SelectLocationViewModel>(
    FragmentSelectLocationBinding::inflate
) {
    override val viewModel: SelectLocationViewModel by viewModels()
    private val db = Firebase.firestore
    private val itemList = mutableListOf<LocationPlace>()
    private lateinit var checkableLocationAdapter: CheckableLocationAdapter

    override fun initView() {
        binding.apply {
            fabCreateNew.setOnClickListener{
                findNavController().safeNavigate(
                    SelectLocationFragmentDirections.actionSelectLocationFragmentToCreateLocationFragment()
                )
            }

            db.collection("locations")
                .get()
                .addOnSuccessListener { locationSnapshot ->
                    val tempList = mutableListOf<LocationPlace>()
                    for(location in locationSnapshot){
                        if(!location.getString("locationName").isNullOrEmpty()){
                            Timber.tag("SelectLocationViewModel").d(location.toObject<LocationPlace>().toString())
                            tempList.add(location.toObject<LocationPlace>())
                        }
                    }
                    itemList.clear()
                    itemList.addAll(tempList)

                    checkableLocationAdapter = CheckableLocationAdapter(itemList)
                    rvLocation.layoutManager = LinearLayoutManager(requireActivity())
                    rvLocation.adapter = checkableLocationAdapter
                    checkableLocationAdapter.onItemClick = {location ->

                    }
                }
        }
    }
}