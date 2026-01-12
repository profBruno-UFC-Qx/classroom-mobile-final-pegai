package com.pegai.app.data.data.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class Location(private val context: Context) {

    private val fusedClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun getLastLocation(onSuccess: (Location) -> Unit) {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let(onSuccess)
            }
    }
}

