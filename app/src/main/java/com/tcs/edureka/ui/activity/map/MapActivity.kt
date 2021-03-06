package com.tcs.edureka.ui.activity.map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tcs.edureka.R
import com.tcs.edureka.ui.fragments.maps.MapFragment
import com.tcs.edureka.ui.fragments.maps.USBFragment
import com.tcs.edureka.utility.Constants
import com.tcs.edureka.utility.Utility

/**
 * @author Bhuvaneshvar
 */
class MapActivity : AppCompatActivity() {
    private val TAG = "MapActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        //Utility.setPreffLocation(null);
        val from = intent.getStringExtra(Constants.EXTRA_DATA_FROM)
        val to = intent.getStringExtra(Constants.EXTRA_DATA_TO)
        val usbView = intent.getStringExtra(Constants.EXTRA_DATA_OPEN_USB)
        val extraShouldOpenMapWithPrefLocation = intent.getStringExtra(Constants.OPEN_MAP_WITH_PREFERRED_LOCATION)
        val fragment: Fragment
        if (extraShouldOpenMapWithPrefLocation != null) {

            if (Utility.getUserPrefLocation() == null) {
                Utility.makeToast("Please fill your preferred location", this)
                super.onBackPressed()
                return
            } else {
                Log.d(TAG, "onCreate: opening with route to preff loc")
                fragment = MapFragment.newInstance(Utility.getPreffLocation())
            }
        } else if (from != null && to != null && from.trim { it <= ' ' }.isNotEmpty() && !to.trim { it <= ' ' }.isEmpty()) {
            fragment = MapFragment.newInstance(from.trim { it <= ' ' },
                    to.trim { it <= ' ' })
            Log.d(TAG, "onCreate: open map with start $from to $to")
        } else if (to != null && !to.trim { it <= ' ' }.isEmpty()) {
            fragment = MapFragment.newInstance("", to.trim { it <= ' ' })
            Log.d(TAG, "onCreate: open with only to $to")
        } else if (usbView != null && !usbView.trim { it <= ' ' }.isEmpty()) {
            fragment = USBFragment.newInstance()
            Log.d(TAG, "onCreate: no data recived opening just usb")
        } else {
            //  Toast.makeText(this, "Destination is required to open this map activity", Toast.LENGTH_SHORT).show()
            //super.onBackPressed()
            //return
            fragment = MapFragment.newInstance()
        }
        openFrag(fragment)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun openFrag(fragment: Fragment) {
        val supportFragmentManager = supportFragmentManager
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}