package com.tcs.edureka.ui.fragments.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.location.*
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.slice.widget.SliceLiveData
import androidx.work.*

import com.github.mjdev.libaums.UsbMassStorageDevice.Companion.getMassStorageDevices
import com.github.mjdev.libaums.fs.FileSystem
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.tcs.edureka.R
import com.tcs.edureka.databinding.FragmentMapBinding
import com.tcs.edureka.model.MapUserModel
import com.tcs.edureka.model.USBDataModel
import com.tcs.edureka.receivers.USBReceiver
import com.tcs.edureka.ui.fragments.maps.viewmodel.MapViewModel
import com.tcs.edureka.utility.NotificationUtil
import com.tcs.edureka.utility.UsbUtil
import com.tcs.edureka.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap

/**
 * @author Bhuvaneshvar
 */
class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    private var param1: Any? = null
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var locationManager: LocationManager? = null
    private val startLocation by lazy { LatLng(12.9778739, 77.590441) }
    private var preffLocation: LatLng? = null
    private var currentUser: String = ""
    private var viewModel: MapViewModel? = null
    private var currentLocation: Location? = null
    private var destinationStarted = false
    private var from = ""
    private var to = ""
    private var isFireStoreListening = false

    private val markerThatWillAlwaysBeThere by lazy {
        HashMap<String, Marker>()
    }
    private val usbReceiver by lazy {
        USBReceiver()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // return back if permissoon is not there
        if (!(Utility.isPermissionGranted(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        || Utility.isPermissionGranted(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION))) {
            Toast.makeText(requireContext(), "Location permission missing", Toast.LENGTH_SHORT).show()
            activity?.onBackPressed()
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        currentUser = getCurrectUser()
        viewModel = MapViewModel()
        preffLocation = getPrefLocation()
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        arguments?.let {
            val get = it.get(EXTRA_DESTINATION)
            param1 = if (get is LatLng) get else null

            if (get !is LatLng) {
                it.getString(EXTRA_FROM)?.let { f ->
                    Log.d(TAG, "onCreate: map frag from $f")
                    from = f
                }
                it.getString(EXTRA_DESTINATION)?.let { t ->
                    Log.d(TAG, "onCreate: map frag to $t")
                    to = t
                }
            }
        }

        if (!(locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER))!!) {
            checkGPSEnable()
        }
        // return to previous activity if GS is not avlbl
        if (!isGoogleServiceAvlbl() || locationManager == null) {
            Toast.makeText(
                    requireContext(),
                    "Location service not found...",
                    Toast.LENGTH_SHORT
            )
                    .show()
            activity?.onBackPressed()
        }

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        // if we have extra data than means we will have to open navigation view by that time show calculating
        binding.locationToReachButton.isEnabled = false

        if (param1 != null) {
            showToast("Please wait, route will appear after getting current location..")
        } else if (from.isNotEmpty() && to.isNotEmpty()) {

            viewModel?.getLocationByName(from, requireContext()) { locA ->
                if (locA == null) {
                    showToast("Location $from not found in map")
                }
                viewModel?.getLocationByName(to, requireContext()) { locB ->
                    if (locB == null) {
                        showToast("location $to not found in map")
                    }
                    if (locA != null && locB != null) {
                        binding.locationToReachButton.isEnabled = true
                    }
                }
            }
        } else if (to.isNotEmpty()) {
            viewModel!!.getLocationByName(to, requireContext()) { to ->
                if (to != null) {
                    showToast("Please wait, route will appear after getting current location..")
                } else {
                    showToast("Location $to not found in map")
                }
            }
        } else {
            showToast("No destination to show route...")
        }

        //slice view
        val sliceView = binding.sliceIcon
        SliceLiveData.fromUri(requireContext(), Utility.getUri(requireContext(), "map"))
                .observe(viewLifecycleOwner, sliceView)

        binding.locationToReachButton.setOnClickListener {
            decideToLaunch()
        }


    }

    private fun decideToLaunch() {
        if (param1 != null && currentLocation != null) {
            val latLng = param1 as LatLng
            val location = Location("Destination")
            location.latitude = latLng.latitude
            location.longitude = latLng.longitude
            showRoute(currentLocation!!, location)

        } else if (from.isNotBlank() && to.isNotBlank()) {
            viewModel!!.getLocationByName(from, requireContext()) { locA ->
                if (locA == null) {
                    showToast("Location $from not found in map")
                } else {
                    viewModel!!.getLocationByName(to, requireContext()) { locB ->
                        if (locB == null) {
                            showToast("location $to not found in map")
                        }
                        if (locB != null) {
                            showRoute(locA, locB)
                        } else {
                            showToast("Can not start route location issue occurred")
                        }
                    }
                }
            }

        } else if (to.isNotBlank() && currentLocation != null) {
            viewModel!!.getLocationByName(to, requireContext()) { locA ->
                if (locA == null) {
                    showToast("Location $to not found in map")
                    showToast("Can not start route")
                } else {
                    showRoute(currentLocation!!, locA)
                }

            }
        } else {
            binding.locationToReachButton.isEnabled = false
        }
    }

    private fun showRoute(start: Location, end: Location) {
        Log.d(TAG, "showRoute: called")
        val distance = start.distanceTo(end) / 1000
        showToast("The distance between ${start.provider} and ${end.provider} is $distance kilometer")

        loadNavigationView("${start.latitude},${start.longitude}",
                "${end.latitude},${end.longitude}")
    }

    private fun showToast(msg: String) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
        }
    }

    // get current user this will be used in firestore save
    private fun getCurrectUser(): String {
        //todo add shared pref call to get current user
        return "Bhuvan".replace(" ", "_", true).toLowerCase().trim()
    }


    // release all receiver
    override fun onDestroyView() {
        super.onDestroyView()
        if (map != null) {
            map!!.setOnMyLocationChangeListener(null)
            map = null
        }
        if (locationManager != null) {
            locationManager = null
        }
        val usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDevice = usbManager.deviceList
        var usb: UsbDevice? = null
        if (usbDevice.size > 0) {
            usbDevice.values.forEach {
                usb = it
                return@forEach
            }
        }

        usb?.let {
            UsbUtil.getPermission(
                    requireContext(),
                    usbManager,
                    usbReceiver,
                    usb!!)
        }

        if (usb != null && currentLocation != null && currentUser.isNotBlank()) {

            var fileSystem: FileSystem? = null
            usb!!.getMassStorageDevices(requireContext()).forEach {
                it.init()
                fileSystem = it.partitions[0].fileSystem
            }

            fileSystem ?: return

            fileSystem.let { fs ->

                val usbM = USBDataModel(currentUser,
                        LatLng(currentLocation!!.latitude, currentLocation!!.longitude),
                        Date(),
                        Utility.getPreffLocation())
                val gson = Gson()
                val toJson = gson.toJson(usbM)

                GlobalScope.launch(Dispatchers.Default) {
                    UsbUtil.writeDate(fileSystem!!, toJson, onComplete = {
                        Log.d(TAG, "onDestroyView: completed $it")
                    }, onFailure = {
                        Log.d(TAG, "onDestroyView: error $it")
                    })
                }
            }
        }

    }

    // show dialog when gps is off
    private fun checkGPSEnable() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id
                    ->
                    dialog.dismiss()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.cancel()
                    activity?.onBackPressed()
                }
        val alert = dialogBuilder.create()
        alert.show()
    }


    // utility to get preff location and update slice
    private fun getPrefLocation(): LatLng? {
        val latLng = Utility.getPreffLocation()
        if (latLng != null) {
            viewModel!!.getLocationByLatLong(latLng.latitude, latLng.longitude, requireContext()) {
                updateSlice(it,
                        "calculating distance", "Please wait checking location", requireContext())
            }
        }
        return latLng
    }

    // this will start navigation between two given location
    private fun loadNavigationView(currLocation: String, desLocation: String) {
        val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + currLocation
                        + "&daddr=" + desLocation + "&dirflg=d"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                and Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        intent.setClassName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity")
        startActivity(intent)
    }

    // test weather google service is avlbl
    private fun isGoogleServiceAvlbl(): Boolean {
        val google = GoogleApiAvailability.getInstance()
        return google.isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS
    }


    // this will get called when map is ready
    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap ?: return
        map?.apply {
            moveCamera(CameraUpdateFactory.newLatLng(startLocation))
            uiSettings.isZoomControlsEnabled = true
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity?.onBackPressed()
                return
            }

            isMyLocationEnabled = true
            uiSettings.isCompassEnabled = true
            var shouldTouchWork = true
            if (from.isBlank()) {
                Log.d(TAG, "onMapReady: $from ${from.isBlank()}")
            } else {
                shouldTouchWork = false
                viewModel!!.getLocationByName(from, requireContext()) { pointA ->
                    if (pointA == null) {
                        showToast("Location $from not found in map")
                    } else {
                        viewModel!!.getLocationByName(to, requireContext()) { pointB ->
                            if (pointB == null) {
                                showToast("location $to not found in map")
                            } else {
                                val mark1 = map?.addMarker(getHueMarker(LatLng(pointA.latitude, pointA.longitude), from))
                                val mar2 = map?.addMarker(getHueMarker(LatLng(pointB.latitude, pointB.longitude), to))
                                val latLngBuilder = LatLngBounds.Builder()
                                latLngBuilder.include(mark1!!.position)
                                latLngBuilder.include(mar2!!.position)

                                val newLatLngBounds = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 0)
                                googleMap.animateCamera(newLatLngBounds)

                                Utility.updateSliceTextAndSubtitle(
                                        "Route in progress",
                                        "Route in progress From $from to $to",
                                        "Tap to open route for your preferred location",
                                        requireContext()
                                )
                                decideToLaunch()
                            }
                        }
                    }
                }
            }

            //prevent user to long click when there is from and to
            setOnMapLongClickListener {
                if (shouldTouchWork)
                    onMapLongClick(it)
            }

            setOnMyLocationChangeListener {
                locationChanged(it)
            }

        }
    }

    private fun locationChanged(location: Location) {
        Log.d(TAG, "locationChanged: changed ")
        markerThatWillAlwaysBeThere["CURRENT_LOCATION"]?.remove()

        if (from.isBlank()) {
            val latLngBuilder = LatLngBounds.Builder()
            latLngBuilder.include(LatLng(location.latitude, location.longitude))
            val newLatLngBounds = CameraUpdateFactory.newLatLngBounds(latLngBuilder.build(), 0)
            map?.animateCamera(newLatLngBounds)
        }
        // update marker
        val currentMarker = MarkerOptions().position(LatLng(location.latitude,
                location.longitude)).title("You are here")
        val addMarker = map?.addMarker(currentMarker)
        markerThatWillAlwaysBeThere["CURRENT_LOCATION"] = addMarker!!

        currentLocation = location

        if (!destinationStarted) {
            if (param1 != null) {
                if (param1 is LatLng) {
                    val addMarkerJi = map?.addMarker(getHueMarker(param1 as LatLng, "Destination"))
                    markerThatWillAlwaysBeThere["TO"] = addMarkerJi!!

                    decideToLaunch()
                    destinationStarted = true
                    binding.locationToReachButton.isEnabled = true
                }
            } else if (from.isBlank() && to.isNotBlank()) {
                viewModel!!.getLocationByName(to, requireContext()) { toLoc ->
                    if (toLoc == null) {
                        showToast("Location $to not found in map")
                    } else {
                        val addMarkerJi = map?.addMarker(getHueMarker(LatLng(toLoc.latitude, toLoc.longitude),
                                "Destination $to"))
                        markerThatWillAlwaysBeThere["TO"] = addMarkerJi!!
                        decideToLaunch()
                        destinationStarted = true
                        binding.locationToReachButton.isEnabled = true
                    }
                }

            }
        }

        //if speed is more than 120km/h alert
        if ((location.speed * 33.33) > 119f) {
            NotificationUtil.notify("Warning! You are going too fast",
                    "You are going at the speed of ${location.speed * 33.33} km/h", requireContext(), 1)
        }

        //if preffered location is given start checking location distance
        preffLocation?.let {
            val loc = Location("PrefLocation")
            loc.latitude = it.latitude
            loc.longitude = it.longitude
            viewModel!!.getLocationByLatLong(it.latitude, it.longitude, requireContext()) { locNam ->
                // send notification
                if (loc.distanceTo(location) <= 500) {
                    NotificationUtil.notify("Location within 500 meter",
                            "Your preferred location $locNam is ${loc.distanceTo(location)} meter away",
                            requireContext())
                }

                //update the distance in slice
                updateSlice(locNam,
                        "is ${loc.distanceTo(location)} meter away",
                        "Tap to goto your preferred location",
                        requireContext())
            }

        }

        if (from.isBlank())
            listenToRideRequest()

    }


    // when clicked long on map
    private fun onMapLongClick(it: LatLng) {
        //request for ride
        if (Utility.getCurrentUserName().trim().isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                    .setPositiveButton("Yes") { d, _ ->
                        val geoPoint = GeoPoint(it.latitude, it.longitude)
                        val mapUser = MapUserModel(Utility.getCurrentUserName(), geoPoint, true)
                        saveMeToRide(mapUser)
                        d.cancel()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.cancel()
                    }
                    .setNeutralButton("Cancel ride request") { dialog, _ ->
                        val geoPoint = GeoPoint(it.latitude, it.longitude)
                        val mapUser = MapUserModel(Utility.getCurrentUserName(), geoPoint, false)
                        saveMeToRide(mapUser)
                        dialog.cancel()
                    }
                    .setCancelable(false)
                    .setTitle("Add Ride Request?")
                    .setMessage("Press yes to Save your ride request on firestore")
                    .show()
        }
    }


    // listen to ride request present in firestore
    private fun listenToRideRequest() {
        if (isFireStoreListening) return

        Log.d(TAG, "listenToRideRequest: on")
        isFireStoreListening = true
        viewModel?.let {
            it.getPeopleOnMapWhoAreLookingForRide(currentUser)
            it.userList.observe(viewLifecycleOwner) { users ->
                map?.clear()

                markerThatWillAlwaysBeThere.forEach { (_, marker) ->
                    map?.addMarker(
                            getHueMarker(marker.position, marker.title!!)
                    )
                }
                users?.forEach { user ->
                    if (user.lookingRide) {
                        val geo = LatLng(user.location.latitude, user.location.longitude)
                        currentLocation?.let { current ->
                            val thisUser = Location(user.userName)
                            thisUser.longitude = geo.longitude
                            thisUser.latitude = geo.latitude
                            if (current.distanceTo(thisUser) <= 100_000L) { //if user within 100km
                                val mark = MarkerOptions().position(geo).title(user.userName + " is looking for ride")
                                map?.addMarker(mark)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getHueMarker(latLang: LatLng, title: String) = MarkerOptions()
            .position(latLang)
            .title(title)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))


    // assign self for ride in firestore so other people can see
    private fun saveMeToRide(mapUser: MapUserModel) {
        viewModel?.setMeForRide(mapUser)?.let {
            it.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "saveMeToRide: saved")
                    Toast.makeText(requireContext(), "Ride Request saved", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Ride Request failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * This will update slice as well as it will notify also
     */
    private fun updateSlice(title: String, subTitle: String, mainTitle: String, context: Context) {
        val substring = StringBuilder()
        if (title.length > 22) substring.append(title.substring(0, 22)).append("...")
        Utility.updateSliceTextAndSubtitle(substring.toString(),
                mainTitle,
                subTitle,
                context)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param from location to start map
         * @param to location of destination
         * @return A new instance of fragment MapFragment.
         */
        @JvmStatic
        fun newInstance(from: String, to: String) =
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_DESTINATION, to)
                        putString(EXTRA_FROM, from)
                    }
                }

        /**
         * @param latLng location to reach
         */
        @JvmStatic
        fun newInstance(latLng: LatLng) =
                MapFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(EXTRA_DESTINATION, latLng)
                    }
                }

        @JvmStatic
        fun newInstance() = MapFragment()

        private const val EXTRA_FROM: String = "FROM"
        private const val TAG = "MapFragment"
        private const val EXTRA_DESTINATION = "TO"
    }


}