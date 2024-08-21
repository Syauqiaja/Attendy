package com.hashtest.attendy.presentation.main.attendance

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.setNavigationResult
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.hashtest.attendy.R
import com.hashtest.attendy.core.Resources
import com.hashtest.attendy.databinding.FragmentAttendanceBinding
import com.hashtest.attendy.domain.models.Attendance
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.domain.models.User
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.dialogs.DialogFailed
import com.hashtest.attendy.presentation.main.dialogs.DialogSuccess
import com.ncorti.slidetoact.SlideToActView
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class AttendanceFragment : BaseFragment<FragmentAttendanceBinding, AttendanceViewModel>(
    FragmentAttendanceBinding::inflate
) {
    override val viewModel: AttendanceViewModel by viewModels()

    private val locationPlace: LocationPlace by lazy { AttendanceFragmentArgs.fromBundle(requireArguments()).locationPlace }
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var mLocationRequest: LocationRequest

    private var userMarker: Marker? = null
    private var officeMarker: Marker? = null

    override fun initView() {
        binding.apply {
            supportMapFragment = childFragmentManager.findFragmentByTag("mapsFragment") as SupportMapFragment
            supportMapFragment.getMapAsync {
                mGoogleMap = it
                addOfficeMarker()
                startLocationUpdates()
            }

            slideButton.onSlideToActAnimationEventListener = actionAnimationEventListener
            slideButton.onSlideCompleteListener = onSlideComplete

            //Deactivate slide button
            slideButton.outerColor = resources.getColor(R.color.grey, null)
            slideButton.text = "Wait for map loading"
            slideButton.isLocked = true //Don't let user check in before their location retrieved

            viewModel.getCurrentAttendance().observe(viewLifecycleOwner){attendance ->
                if(attendance != null){
                    tvPunchIn.text = convertTimeMillisToTimeFormat(attendance.checkInTime)
                    tvPunchIn.setTextColor(resources.getColor(R.color.black, null))

                    tvPunchOut.text = "NOW"
                    tvPunchOut.setTextColor(resources.getColor(R.color.primary, null))
                }
            }
        }
    }

    fun convertTimeMillisToTimeFormat(timeMillis: Long): String {
        val date = Date(timeMillis)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return format.format(date)
    }

    private fun addOfficeMarker() {
        val latLng = LatLng(locationPlace.latitude, locationPlace.longitude)
        officeMarker = mGoogleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(locationPlace.locationName)
        )
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(19f))
        binding.tvLocationTitle.text = locationPlace.locationName
        binding.tvLocation.text = locationPlace.locationAddress

        val circleOptions = CircleOptions()
            .center(latLng)
            .radius(50.0)
            .fillColor(Color.argb(16, 0,117, 255))
            .strokeColor(Color.BLUE)
            .strokeWidth(2f)
        mGoogleMap.addCircle(circleOptions)
    }

    private fun checkPermission():Boolean{
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
            return false
        }
        return true
    }

    // 3.
    protected fun startLocationUpdates() {
        // initialize location request object
        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .setMaxUpdates(1)
            .build()

        // initialize location setting request builder object
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // initialize location service object
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // call register location listener
        registerLocationListener()
    }

    private fun registerLocationListener() {
        binding.progressBar.visibility = View.VISIBLE
        // initialize location callback object
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                binding.progressBar.visibility = View.GONE
                locationResult.lastLocation?.let { onLocationChanged(it) }
            }
        }
        // 4. add permission if android version is greater then 23
        if(checkPermission()) {
            LocationServices.getFusedLocationProviderClient(requireActivity())
                .requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper())
        }
    }

    private fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)

        updateMarker(latLng)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun updateMarker(latLng: LatLng){
        userMarker?.remove()
        userMarker = mGoogleMap.addMarker(MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person))
            .title("Current Location"))


        val lineOptions = PolylineOptions()
            .add(userMarker!!.position)
            .add(officeMarker!!.position)
            .color(Color.RED)
            .width(3f)
        mGoogleMap.addPolyline(lineOptions)

        if(calculateDistance() > 50){ //Reject attendance
            binding.slideButton.isAnimateCompletion = false
        }else{ //Accept attendance
            binding.slideButton.isAnimateCompletion = true
        }

        binding.apply { //Activate slide button
            slideButton.outerColor = resources.getColor(R.color.primary, null)
            slideButton.text = getString(R.string.swipe_to_punch_in)
            slideButton.isLocked = false
        }
    }

    private fun calculateDistance(): Float {
        val lat1 = userMarker!!.position.latitude; val lat2 = officeMarker!!.position.latitude
        val lon1 = userMarker!!.position.longitude; val lon2 = officeMarker!!.position.longitude

        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] // Distance in meters
    }


    private val locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        result.forEach { (keys, isGranted) ->
            when(keys){
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION ->{
                    if (!isGranted) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                showNotificationPermissionRationale("Location permission is needed", arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                            } else {
                                showSettingDialog("Location Permission", "Location permission is needed, Please grant a permission")
                            }
                        }
                    }else{
                        startLocationUpdates()
                    }
                }
            }
        }
    }

    private fun showSettingDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showNotificationPermissionRationale(message: String, permissions: Array<String>) {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Warning")
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    locationPermissionLauncher.launch(permissions)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val actionAnimationEventListener = object: SlideToActView.OnSlideToActAnimationEventListener{
        override fun onSlideCompleteAnimationEnded(view: SlideToActView) {}
        override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {
            if(calculateDistance() <= 50){ //Accept Attendance
                viewModel.createAttendance().observe(viewLifecycleOwner){response ->
                    when(response){
                        is Resources.Error -> {Timber.tag("AttendanceFragment").e(response.message)}
                        is Resources.Loading -> {}
                        is Resources.Success ->{
                            setNavigationResult(true, "attendance")
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
        override fun onSlideResetAnimationEnded(view: SlideToActView) {}
        override fun onSlideResetAnimationStarted(view: SlideToActView) {}
    }
    private val onSlideComplete=object : SlideToActView.OnSlideCompleteListener{
        override fun onSlideComplete(view: SlideToActView) {
            if(calculateDistance() > 50){ //Reject Attendance
                val dialog = DialogFailed(title = "You're outside the location"
                    , description = "To make the check in work, you must at least 50 meters near the center")
                dialog.show(requireActivity().supportFragmentManager, "Attendance failed dialog")

                view.setCompleted(false, false)
            }
        }
    }
}