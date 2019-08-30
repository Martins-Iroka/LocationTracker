package com.martdev.android.androidtracker

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.StreetViewPanoramaOptions
import com.google.android.gms.maps.model.MapStyleOptions
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var lat = 0.0
    private var long = 0.0
    private var address = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .add(R.id.map, mapFragment).commit()
        mapFragment.getMapAsync(this)

        lat = intent.getDoubleExtra(LATITUDE, 0.0)
        long = intent.getDoubleExtra(LONGITUDE, 0.0)
        address = intent.getStringExtra(ADDRESS)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(location).title(address))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

//        // Add a ground overlay 100 meters in width to the home location.
//        val homeOverlay = GroundOverlayOptions()
//            .image(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_dialog_map))
//            .position(location, 100f)
//
//        mMap.addGroundOverlay(homeOverlay)

        setMapLongClick(mMap) // Set a long click listener for the map;
        setPoiClick(mMap) // Set a click listener for points of interest.
        enableMyLocation(mMap); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
        setInfoWindowClickToPanorama(mMap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.map_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.normal_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                return true
            }
            R.id.hybrid_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                return true
            }
            R.id.satellite_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                return true
            }
            R.id.terrain_map -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * Adds a blue marker to the map when the user long clicks on it.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private fun setMapLongClick(map: GoogleMap) {

        // Add a blue marker to the map when the user performs a long click.
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                getString(R.string.lat_long_snippet),
                latLng.latitude,
                latLng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )
        }
    }

    /**
     * Adds a marker when a place of interest (POI) is clicked with the name of
     * the POI and immediately shows the info window.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker.showInfoWindow()
            poiMarker.tag = getString(R.string.poi)
        }
    }

    /**
//     * Loads a style from the map_style.json file to style the Google Map. Log
//     * the errors if the loading fails.
//     *
//     * @param map The GoogleMap object to style.
//     */
//    private fun setMapStyle(map: GoogleMap) {
//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            val success = map.setMapStyle(
//                MapStyleOptions.loadRawResourceStyle(
//                    this, R.raw.map_style
//                )
//            )
//
//            if (!success) {
//                Log.e("Map", "Style parsing failed.")
//            }
//        } catch (e: Resources.NotFoundException) {
//            Log.e("Map", "Can't find style. Error: ", e)
//        }
//
//    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private fun enableMyLocation(map: GoogleMap) {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
    }

    /**
     * Starts a Street View panorama when an info window containing the poi tag
     * is clicked.
     *
     * @param map The GoogleMap to set the listener to.
     */
    private fun setInfoWindowClickToPanorama(map: GoogleMap) {
        map.setOnInfoWindowClickListener { marker ->
            // Check the tag
            if (marker.tag == "poi") {

                // Set the position to the position of the marker
                val options = StreetViewPanoramaOptions().position(
                    marker.position
                )

                val streetViewFragment = SupportStreetViewPanoramaFragment
                    .newInstance(options)

                // Replace the fragment and add it to the backstack
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.map,
                        streetViewFragment
                    )
                    .addToBackStack(null).commit()
            }
        }
    }

    companion object {

        private const val LATITUDE = "com.martdev.android.androidtracker.latitude"
        private const val LONGITUDE = "com.martdev.android.androidtracker.longitude"
        private const val ADDRESS = "com.martdev.android.androidtracker.address"

        fun newIntent(context: Context, lat: Double, long: Double, address: String): Intent {
            return Intent(context, MapsActivity::class.java).apply {
                putExtra(LATITUDE, lat)
                putExtra(LONGITUDE, long)
                putExtra(ADDRESS, address)
            }
        }
    }
}
