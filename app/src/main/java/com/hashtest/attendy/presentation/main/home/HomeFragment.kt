package com.hashtest.attendy.presentation.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.utils.changeStatusBarTo
import com.aglotest.algolist.utils.clearNavigationResult
import com.aglotest.algolist.utils.getNavigationResult
import com.aglotest.algolist.utils.minutesToTime
import com.aglotest.algolist.utils.safeNavigate
import com.aglotest.algolist.utils.scaleViewOneShot
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firestore.v1.Cursor
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.FragmentHomeBinding
import com.hashtest.attendy.domain.models.Attendance
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.domain.models.User
import com.hashtest.attendy.presentation.adapters.AttendanceListAdapter
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.dialogs.BottomSheetSelectLocation
import com.hashtest.attendy.presentation.main.dialogs.DialogSuccess
import timber.log.Timber


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(
    FragmentHomeBinding::inflate
) {
    override val viewModel: HomeViewModel by viewModels()
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun initView() {
        requireActivity().changeStatusBarTo(resources.getColor(R.color.primary, null), false)

        binding.apply {
            tvName.text = auth.currentUser!!.displayName
            fabDoAttendance.isEnabled = false

            btnChangeLocation.setOnClickListener {
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToSelectLocationFragment()
                )
            }
            btnSelectLocation.setOnClickListener {
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToSelectLocationFragment()
                )
            }
            btnProfile.setOnClickListener {
                findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
            }

            //Observe user location
            viewModel.userLocation.observe(viewLifecycleOwner){userLocation->
                if(userLocation != null){
                    setHeaderWithLocation(userLocation)

                    fabDoAttendance.setOnClickListener {
                        fabDoAttendance.scaleViewOneShot(0.95f, 75) //Add click effect
                        findNavController().safeNavigate(
                            HomeFragmentDirections.actionHomeFragmentToAttendanceFragment(userLocation)
                        )
                    }
                    fabDoAttendance.isEnabled = true
                }else{
                    fabDoAttendance.isEnabled = false
                    setNoLocation()
                }
            }
            viewModel.getUserLocation()

            rvAttendances.layoutManager = LinearLayoutManager(requireContext())
            rvAttendances.setHasFixedSize(true)
            //Observe attendances
            viewModel.attendances.observe(viewLifecycleOwner){attendances ->
                if(!attendances.isNullOrEmpty()){
                    rvAttendances.adapter = AttendanceListAdapter(attendances.toMutableList())
                }
            }
            viewModel.getAllAttendances()

            getNavigationResult<Boolean>("attendance")?.observe(viewLifecycleOwner){
                if(it == true){
                    val dialog = DialogSuccess()
                    dialog.show(childFragmentManager, "Dialog attendance success")
                }
                clearNavigationResult<Boolean>("attendance")
            }
        }
    }

    private fun setHeaderWithLocation(location: LocationPlace) {
        binding.apply {
            tvLocationName.text = location.locationName
            tvCheckInDue.text = minutesToTime(location.openTime)
            tvCheckOutStarts.text = minutesToTime(location.closeTime)

            layoutEmptyLocation.visibility = View.GONE
            layoutLocation.visibility = View.VISIBLE
            tvLocationName.visibility = View.VISIBLE
        }
    }

    private fun setNoLocation() {
        binding.layoutEmptyLocation.visibility = View.VISIBLE
        binding.tvLocationName.visibility = View.GONE
        binding.layoutLocation.visibility = View.GONE
    }
}