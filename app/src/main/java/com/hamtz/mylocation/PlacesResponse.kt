package com.hamtz.mylocation

import com.google.android.gms.maps.model.LatLng

data class PlacesResponse(    val geometry: Geometry,
                              val name: String,
                              val vicinity: String,
                              val rating: Float
) {

    data class Geometry(
        val location: GeometryLocation
    )

    data class GeometryLocation(
        val lat: Double,
        val lng: Double
    )
}

fun PlacesResponse.toPlace(): Place = Place(
    name = name,
    latLng = LatLng(geometry.location.lat, geometry.location.lng),
    address = vicinity,
    rating = rating
)