package com.hamtz.mylocation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        tvLatitude = findViewById(R.id.tv_latitude)
        tvLongitude = findViewById(R.id.tv_logitude)

        getCurrentLocation()
    }

    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    private fun getCurrentLocation() {
        if(checkPermissions()){
            if (isLocationEnabled()){
                //final latitude and logitude code here
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                   requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location:Location?=task.result
                    if (location == null){
                        Toast.makeText(this,"Null Recieved",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Get Success",Toast.LENGTH_SHORT).show()
                        tvLatitude.text=""+location.latitude
                        tvLongitude.text=""+location.longitude

//                        locLatitude= location.latitude
//                        locLongitude=location.longitude

                        val mapFragment = supportFragmentManager.findFragmentById(
                            R.id.map_fragment
                        ) as? SupportMapFragment
                        mapFragment?.getMapAsync { googleMap ->
                            addMarkers(googleMap, location.latitude, location.longitude)

                        }
                    }
                }


            }else{
                //setting open here
                Toast.makeText(this,"Turn on location",Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{
            //request prermission here
            requestPermission()
        }
    }

    private fun addMarkers(googleMap: GoogleMap, latitude: Double, longitude: Double) {
        mMap = googleMap

        val myLocation = LatLng(latitude,longitude)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Lokasi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,20.0f))
        Toast.makeText(this,""+latitude+"  "+longitude,Toast.LENGTH_SHORT).show()
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
            )
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERSMISSION_REQUEST_ACCESS_LOCATION)
    }

    companion object{
        private const val PERSMISSION_REQUEST_ACCESS_LOCATION=100
    }
    private fun checkPermissions(): Boolean{
        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== PERSMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            }else{
                Toast.makeText(applicationContext,"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

}
