package com.tcs.edureka.ui.fragments.maps

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mjdev.libaums.UsbMassStorageDevice.Companion.getMassStorageDevices
import com.github.mjdev.libaums.fs.FileSystem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.tcs.edureka.R
import com.tcs.edureka.databinding.FragmentUSBBinding
import com.tcs.edureka.model.USBDataModel
import com.tcs.edureka.receivers.USBInterface
import com.tcs.edureka.receivers.USBReceiver
import com.tcs.edureka.utility.UsbUtil
import com.tcs.edureka.utility.Utility
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


/**
 * @author Bhuvaneshvar
 */
class USBFragment : Fragment(R.layout.fragment_u_s_b), OnMapReadyCallback, USBInterface {


    private var _binding: FragmentUSBBinding? = null
    val binding get() = _binding!!
    private var map: GoogleMap? = null
    private val usbReceiver by lazy {
        USBReceiver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUSBBinding.bind(view)
        binding.mapViewContainer.visibility = GONE

        val usbManager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        usbReceiver.setUsbInterface(this)
        val deviceList: HashMap<String, UsbDevice> = usbManager.deviceList
        if (usbManager.deviceList.isEmpty()) {
            Toast.makeText(requireContext(), "No USB device found", Toast.LENGTH_LONG).show()
            binding.withoutUsb.visibility = VISIBLE
            binding.withUsbData.visibility = GONE
        } else {
            var usbDevice: UsbDevice? = null
            for (usbDevice1 in deviceList.values) {
                usbDevice = usbDevice1
                break
            }

            UsbUtil.getPermission(
                    requireContext(),
                    usbManager,
                    usbReceiver,
                    usbDevice!!)

            var fileSystem: FileSystem? = null
            usbDevice.getMassStorageDevices(requireContext()).forEach {
                it.init()
                fileSystem = it.partitions[0].fileSystem
            }

            if (usbManager.hasPermission(usbDevice) && fileSystem != null) {
                binding.withoutUsb.visibility = GONE
                binding.withUsbData.visibility = VISIBLE
                val supportMapFragment = childFragmentManager.findFragmentById(R.id.mapViewUSB)
                        as SupportMapFragment
                supportMapFragment.getMapAsync(this@USBFragment)

                binding.btnRead.setOnClickListener {
                    GlobalScope.launch(Dispatchers.Default) {
                        UsbUtil.readData(fileSystem!!, onComplete = { data ->
                            Log.d(TAG, "onViewCreated: show data $data")
                            showData(data)
                        }, onFailure = {
                            showData("error")
                            Log.d(TAG, "onViewCreated: error read $it")
                        })
                    }
                }
            }

        }
    }

    private fun showData(data: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val json = JSONObject(data)
                val gson = Gson()
                val usbDataModel = gson.fromJson(json.toString(), USBDataModel::class.java)
                Log.d(TAG, "showData: $usbDataModel")
                binding.mapViewContainer.visibility = VISIBLE
                map?.apply {
                    addMarker(MarkerOptions().title("Last location of ${usbDataModel.userName} as of ${usbDataModel.lastAccessDate}")
                            .position(usbDataModel.lastLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                    usbDataModel?.preffLocation?.let {
                        addMarker(MarkerOptions().title("Preferred location of ${usbDataModel.userName}")
                                .position(it))
                    }
                    moveCamera(CameraUpdateFactory.newLatLng(usbDataModel.lastLocation))

                }
                var prfLoc = "Not set"
                usbDataModel.preffLocation?.let {
                    prfLoc = getLocationName(it.latitude, it.longitude)
                }

                binding.withUsbTV.text = "User ${usbDataModel.userName} was last seen at " +
                        "${usbDataModel.lastLocation} \n" +
                        "(${getLocationName(usbDataModel.lastLocation.latitude, usbDataModel.lastLocation.longitude)})" +
                        " last access ${usbDataModel.lastAccessDate}, prefferred location was $prfLoc"

            } catch (error: Exception) {
                Log.d(TAG, "showData: ${error.message}")
                Toast.makeText(requireContext(), "Malformed data", Toast.LENGTH_LONG).show()
                binding.withUsbTV.text = "USB data malformed"
            }
        }
    }

    private fun getLocationName(lat: Double, long: Double): String {
        val geo = Geocoder(requireContext())
        val fromLocation = geo.getFromLocation(lat, long, 1)
        val builder = StringBuilder()
        fromLocation?.let {
            if (it.size > 0) {
                for (i in 0..it[0].maxAddressLineIndex) {
                    builder.append(it[0].getAddressLine(i)).append("\n")
                }
            }
        }

        return builder.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                USBFragment().apply {
                    arguments = Bundle().apply {
                    }
                }

        private const val TAG = "USBFragment"

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        p0.apply {
            uiSettings.isZoomControlsEnabled = true
            setOnMarkerClickListener {
                markerCicked(it)
                false
            }
        }
    }

    private fun markerCicked(it: Marker) {

    }

    override fun onUsbAttached() {
        Utility.makeToast("Usb Attached", requireContext())
    }

    override fun onUsbDetached() {
        Utility.makeToast("Usb Detached", requireContext())
    }

    override fun onUsbPermissionGranted() {
        Utility.makeToast("Usb Permission granted", requireContext())
    }

    override fun onUsbPermissionDenied() {
        Utility.makeToast("Usb Permission denied", requireContext())
    }
}