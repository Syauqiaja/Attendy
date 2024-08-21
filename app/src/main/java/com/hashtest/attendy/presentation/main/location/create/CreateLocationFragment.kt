package com.hashtest.attendy.presentation.main.location.create

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.aglotest.algolist.utils.scaleViewOneShot
import com.aglotest.algolist.utils.showCustomSnackBar
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.hashtest.attendy.R
import com.hashtest.attendy.databinding.BottomSheetTimePickerBinding
import com.hashtest.attendy.databinding.FragmentCreateLocationBinding
import com.hashtest.attendy.domain.models.LocationPlace
import com.hashtest.attendy.presentation.base.BaseFragment
import com.hashtest.attendy.presentation.main.dialogs.BottomSheetTimePicker
import timber.log.Timber
import java.io.IOException
import java.util.Locale

class CreateLocationFragment : BaseFragment<FragmentCreateLocationBinding, CreateLocationViewModel>(
    FragmentCreateLocationBinding::inflate
), OnMapReadyCallback {
    override val viewModel: CreateLocationViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mGoogleMap : GoogleMap
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var supportMapFragment: SupportMapFragment

    private val db = Firebase.firestore

    private var addresses: MutableList<android.location.Address>? = null
    private var marker: Marker? = null

    override fun initView() {
        supportMapFragment = childFragmentManager.findFragmentByTag("mapFragment") as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        startLocationUpdates()

        binding.apply {
            btnSave.setOnClickListener {
                if(validateFields()){
                    val locationPlace = LocationPlace(
                        locationName = edtLocationName.text.toString(),
                        locationAddress = edtLocationAddress.text.toString(),
                        description = edtLocationDescription.text.toString(),
                        latitude = marker!!.position.latitude,
                        longitude = marker!!.position.longitude
                    )
                    db.collection("locations")
                        .document()
                        .set(locationPlace)
                        .addOnSuccessListener {
                            findNavController().popBackStack()
                        }.addOnFailureListener {
                            requireContext().showCustomSnackBar(getString(R.string.failed_to_add_new_location), root)
                        }
                }
            }
            edtCheckin.setOnClickListener {
                val dialog = BottomSheetTimePicker(object : BottomSheetTimePicker.OnButtonClicked{
                    override fun onCancel() {}

                    override fun onSave(hour: Int, minute: Int, am: String) {
                        edtCheckin.setText(String.format("%02d", hour)+":"+String.format("%02d", minute)+" "+am)
                    }
                })
                dialog.show(childFragmentManager, "Checkin Timepicker")
            }
            edtCheckout.setOnClickListener {
                val dialog = BottomSheetTimePicker(object : BottomSheetTimePicker.OnButtonClicked{
                    override fun onCancel() {}

                    override fun onSave(hour: Int, minute: Int, am: String) {
                        edtCheckout.setText(String.format("%02d", hour)+":"+String.format("%02d", minute)+" "+am)
                    }
                })
                dialog.show(childFragmentManager, "Checkout Timepicker")
            }
        }
    }

    private fun validateFields(): Boolean {
        binding.apply {
            if(edtLocationAddress.length() == 0){
                requireContext().showCustomSnackBar("Fill all the required fields", root)
                return false
            }
            if(edtLocationDescription.length() == 0){
                requireContext().showCustomSnackBar("Fill all the required fields", root)
                return false
            }
            if (edtLocationName.length() ==0) {
                requireContext().showCustomSnackBar("Fill all the required fields", root)
                return false
            }
            if(marker == null){
                requireContext().showCustomSnackBar("Please pin a place first!", root)
                return false
            }
        }
        return true
    }

    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if(!checkPermission()) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Timber.tag("CreateLocationFragment").d("${location.latitude}, ${location.longitude}")
                } else {
                    Timber.tag("CreateLocationFragment").e("Location is null")
                }
            }
            .addOnFailureListener { exception ->
                Timber.tag("CreateLocationFragment").e(exception, "Failed to get location")
            }
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
            notificationPermissionLauncher.launch(arrayOf(
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
        // create message for toast with updated latitude and longitudefa
        var msg = "Updated Location: " + location.latitude  + " , " +location.longitude

        // show toast message with updated location
        //Toast.makeText(this,msg, Toast.LENGTH_LONG).show()
        val location = LatLng(location.latitude, location.longitude)

        mGoogleMap.clear()
        updateMarker(location)
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(location))

        mGoogleMap.setOnMapClickListener { latlng ->
            if(!binding.btnLock.isChecked){
                updateMarker(latlng)
            }else{
                binding.btnLock.scaleViewOneShot(1.1f, 100)
            }
        }
    }

    private fun updateMarker(latLng: LatLng){
        marker?.remove()
        marker = mGoogleMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))

        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            binding.edtLocationAddress.setText(addresses?.get(0)?.getAddressLine(0))
            binding.tvLatitude.text = getString(R.string.latitude_d, latLng.latitude)
            binding.tvLongitude.text = getString(R.string.longitude_d, latLng.longitude)
        }catch (e: IOException) {
            e.printStackTrace();
        }
    }


    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        result.forEach { (keys, isGranted) ->
            when(keys){
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION ->{
                    if (!isGranted) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                showNotificationPermissionRationale("Izin lokasi diperlukan, untuk menampilkan lokasi", arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
                            } else {
                                showSettingDialog("Location Permission", "Diperlukan izin lokasi, Harap izinkan izin lokasi dari pengaturan")
                            }
                        }
                    }else{
                        getCurrentLocation()
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
            .setNegativeButton("Batalkan", null)
            .show()
    }

    private fun showNotificationPermissionRationale(message: String, permissions: Array<String>) {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Peringatan")
            .setMessage(message)
            .setPositiveButton("Ok") { _, _ ->
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(permissions)
                }
            }
            .setNegativeButton("Batalkan", null)
            .show()
    }

    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map
        Timber.tag("CreateLocationFragment").d("%s | %s", map.cameraPosition.target.latitude.toString(), map.cameraPosition.target.latitude.toString())
    }
}