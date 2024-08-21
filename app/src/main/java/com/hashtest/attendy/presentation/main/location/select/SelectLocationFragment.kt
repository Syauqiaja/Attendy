package com.hashtest.attendy.presentation.main.location.select

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.aglotest.algolist.utils.safeNavigate
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
    private val auth = FirebaseAuth.getInstance()
    private val itemList = mutableMapOf<LocationPlace, String>()
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
                    itemList.clear()
                    for(location in locationSnapshot){
                        if(!location.getString("locationName").isNullOrEmpty()){
                            Timber.tag("SelectLocationViewModel").d(location.toObject<LocationPlace>().toString())
                            itemList[location.toObject<LocationPlace>()] = location.id
                        }
                    }

                    checkableLocationAdapter = CheckableLocationAdapter(itemList)
                    rvLocation.layoutManager = LinearLayoutManager(requireActivity())
                    rvLocation.adapter = checkableLocationAdapter
                    checkableLocationAdapter.onItemClick = {locationRef ->
                        setUserCurrentLocation(locationRef)
                    }
                }
        }
    }

    private fun setUserCurrentLocation(locationRef: String) {
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .set(hashMapOf(
                "locationRef" to locationRef
            )).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    findNavController().popBackStack()
                }else{
                    requireContext().showCustomSnackBar("Faile getting new location", binding.root)
                }
            }
    }
}