package com.tcs.edureka.model

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class USBDataModel(val userName: String = "",
                        val lastLocation: LatLng,
                        val lastAccessDate: Date,
                        val preffLocation: LatLng? = null)