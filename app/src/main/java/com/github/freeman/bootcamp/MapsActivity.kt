package com.github.freeman.bootcamp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.github.freeman.bootcamp.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val epfl = LatLng(46.520536, 6.568318)
        val sat = LatLng(46.520544, 6.567825)
        val marker = mMap.addMarker(MarkerOptions().position(sat).title("Satellite"))
        if (marker != null) {
            marker.tag = getString(R.string.satellite_marker_tag)
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(epfl, 14f))
        mMap.setMaxZoomPreference(15f)
        mMap.setOnMarkerClickListener { clickedMarker ->
            if (clickedMarker == marker) {
                Toast.makeText(applicationContext, "Coordinates: " + marker.position.toString(), Toast.LENGTH_LONG).show()
            }
            false
        }
    }
}