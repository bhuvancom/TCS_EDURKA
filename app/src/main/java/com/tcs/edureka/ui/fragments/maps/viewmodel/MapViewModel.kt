package com.tcs.edureka.ui.fragments.maps.viewmodel

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.tcs.edureka.model.MapUser
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.util.*

class MapViewModel : ViewModel(), EventListener<QuerySnapshot> {
    private val _userList = MutableLiveData<MutableList<MapUser>>()
    val userList: LiveData<MutableList<MapUser>> = _userList
    var currentUserName = ""
    val url = "https://maps.googleapis.com/maps/api/directions/json?"
    val key = "AIzaSyCHeKBNDMdv7aICUW88LFDcFoI0FZm13vU"

    /**
     * @param origin should be comma seprated lat,lang
     * @param destination should also follow same
     */
    fun getDirectionData(origin: String, destination: String, onComplete: (JSONObject?) -> Unit) {
        val urlForDirection = "${url}origin=$origin&destination=$destination&key=$key"
        //call back to have result there
        //"https://randomuser.me/api/"
        Log.d(TAG, "getDirectionData: $urlForDirection")
        viewModelScope.launch {
            doActualJsonDownload(urlForDirection) {
                onComplete(it)
            }
        }

    }

    fun getLocationByLatLong(lat: Double, longi: Double, context: Context, onCompelete: (String) -> Unit) {
        viewModelScope.launch {
            GlobalScope.launch(Dispatchers.IO) {
                val geo = Geocoder(context)
                val fromLocation = geo.getFromLocation(lat, longi, 1)
                val builder = StringBuilder()
                fromLocation?.let {
                    if (it.size > 0) {
                        for (i in 0..it[0].maxAddressLineIndex) {
                            builder.append(it[0].getAddressLine(i)).append("\n")
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    onCompelete(builder.toString())
                }
            }
        }
    }

    fun getLocationByName(locName: String, context: Context, onComplete: (Location?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val location = Location(locName.trim())
            val geoCoder = Geocoder(context, Locale.getDefault())
            try {
                val fromLocationName: List<Address> = geoCoder.getFromLocationName(locName, 1)
                if (fromLocationName.isNotEmpty()) {
                    val address = fromLocationName[0]
                    location.longitude = address.longitude
                    location.latitude = address.latitude
                    withContext(Dispatchers.Main) {
                        onComplete(location)
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "getLocationByName: error " + e.message)
                withContext(Dispatchers.Main) {
                    onComplete(null)
                }
            }
        }
    }


    private suspend fun doActualJsonDownload(urlStr: String, onComplete: (JSONObject?) -> Unit) {
        val job = CoroutineScope(Dispatchers.IO).async {
            var httpConnect: URLConnection? = null
            var inputStream: BufferedInputStream? = null
            val data = StringBuilder()
            try {
                var url = URL(urlStr)
                httpConnect = url.openConnection() as URLConnection
                httpConnect.connect()
                inputStream = BufferedInputStream(url.openStream())
                val buffReader = BufferedReader(InputStreamReader(inputStream))
                buffReader.readLines().forEach {
                    data.append(it).append("\n")
                }
                buffReader.close()
                JSONObject(data.toString())
            } catch (error: Exception) {
                Log.d(TAG, "doActualJsonDownload: error ${error.message}")
                error.printStackTrace()
                null
            } finally {
                inputStream?.close()
            }
        }
        val await = job.await()
        onComplete(await)

    }

    fun getPeopleOnMapWhoAreLookingForRide(me: String) {
        _userList.value = mutableListOf()
        val firestore = FirebaseFirestore.getInstance().collection("users")
        currentUserName = me

        firestore.addSnapshotListener(this)
    }

    fun setMeForRide(mapUser: MapUser): Task<Void> {
        currentUserName = mapUser.userName
        return FirebaseFirestore.getInstance().collection("users")
                .document(mapUser.userName.replace(" ", "_", true).toLowerCase())
                .set(mapUser)
    }


    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
        value?.let {
            it.documentChanges
                    .forEach { documentChange ->

                        val mapUser = documentChange.document.toObject(MapUser::class.java)
                        when (documentChange.type) {
                            DocumentChange.Type.ADDED -> {
                                if (!mapUser.userName.contentEquals(currentUserName)) {
                                    Log.d(TAG, "onEvent: added ${userList.value}")
                                    _userList.value!!.add(mapUser)

                                }
                            }
                            DocumentChange.Type.MODIFIED -> {
                                if (!mapUser.userName.contentEquals(currentUserName)) {
                                    val find = _userList.value?.find { user ->
                                        user.userName == mapUser.userName
                                    }
                                    val indexOf = _userList.value?.indexOf(find)
                                    indexOf?.let { indexO ->
                                        if (indexO >= 0) {
                                            _userList.value?.set(indexO, mapUser)
                                            Log.d(TAG, "onEvent: modified ${userList.value}")
                                        }
                                    }
                                }
                            }

                            DocumentChange.Type.REMOVED -> {
                                val find = _userList.value?.find { user ->
                                    user.userName.contentEquals(mapUser.userName)
                                }
                                val index = _userList.value?.indexOf(find)
                                index?.let { inde ->
                                    if (inde >= 0) {
                                        _userList.value?.removeAt(inde)
                                        Log.d(TAG, "onEvent: removed ${userList.value}")
                                    }
                                }
                            }
                        }
                    }

            _userList.postValue(_userList.value)
        }
    }

    companion object {
        private const val TAG = "MapViewModel"
    }
}