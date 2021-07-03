package com.tcs.edureka.model

import com.google.firebase.firestore.GeoPoint
import java.io.Serializable
import java.util.*

data class MapUser(val userName: String = "",
                   var location: GeoPoint = GeoPoint(0.0, 0.0),
                   var lookingRide: Boolean = false,
                   var lastUpdate: Date = Date()) : Serializable