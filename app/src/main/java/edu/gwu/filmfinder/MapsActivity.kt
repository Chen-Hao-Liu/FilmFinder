package edu.gwu.filmfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.jetbrains.anko.doAsync
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var confirm: Button
    private lateinit var currentLocation: ImageButton
    private var currentAddress:Address? = null
    private lateinit var locationProvider: FusedLocationProviderClient

    private var currLatLng: LatLng? = LatLng(38.8993339, -77.0500718)
    val markerList = mutableMapOf<Marker, String>()
    var currMarker: Marker? = null


    companion object {
        val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        confirm = findViewById(R.id.confirm)
        confirm.isEnabled = false
        confirm.setOnClickListener{
            if (currMarker!= null) {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val url = markerList.get(currMarker!!)
                val content_url = Uri.parse(url)
                intent.data = content_url
                if (intent.resolveActivity(packageManager) != null) {
                    markerList.clear()
                    startActivity(intent)
                }
            }
        }


        currentLocation = findViewById(R.id.current_location)
        currentLocation.setOnClickListener{
            checkPermission()
        }

        // Triggers the map to load, will call onMapReady when complete
        mapFragment.getMapAsync(this)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            // We only need one location update, so we can stop listening for updates now.
            // Otherwise, this function would be called repeatedly with new updates.
            locationProvider.removeLocationUpdates(this)

            // Get most recent result (index 0)
            val mostRecent = locationResult.locations[0]
            val latitude = mostRecent.latitude
            val longtitude = mostRecent.longitude
            val latLng = LatLng(latitude,longtitude)
            doGeoCoding(latLng)
        }
    }

    fun useCurrentLocation(){
        val locationRequest = LocationRequest()
        locationRequest.interval = 1000
        locationRequest.priority = PRIORITY_HIGH_ACCURACY

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    fun doGeoCoding(latLng: LatLng) {
        doAsync {
            val geocoder = Geocoder(this@MapsActivity)
            val result: List<Address> = try{
                geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    10
                )
            }catch (exception:Exception){
                Log.d(TAG,"Failed to retrieve address result ${exception} ")
                listOf<Address>()
            }
            if (result.isNotEmpty()){
                val firstResult = result.first()
                val streetAddress = firstResult.getAddressLine(0)
                currentAddress = firstResult

                runOnUiThread{

                }
            }
        }
    }

    fun checkPermission(){
        val permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (permissionState){
            // Permission granted - we can now access the GPS
            Log.d(TAG, " GPS Permission granted")
            useCurrentLocation()
        } else {
            // Permission is not granted
            Log.d(TAG, " GPS Permission denied")

            // Ask for Permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
        }
    }

    //call when user nether grants or denies the permission prompt
    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            // This is the result of our GPS permission prompt

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // user granted the permission prompt
                Log.d(TAG, " GPS Permission granted")
                useCurrentLocation()
            } else {
                //User denies the GPS permission or we had an automatic denial by the system
                Log.d(TAG, "GPS Permission denied")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                        // User denied, but can still be re-prompted, like regular deny
                    } else {
                        // User denied can don't want to be prompted again, like deny + don't ask again
                        Toast.makeText(
                            this,
                            "To use this feature, go into your settings and enable the location permission",
                            Toast.LENGTH_LONG
                        ).show()

                        val settingsIntent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:$packageName")
                        )
                        settingsIntent.addCategory(Intent.CATEGORY_DEFAULT)
                        settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivityForResult(settingsIntent, 100)
                    }
                }
            }
        }

        @Override fun PreferenceManager.OnActivityResult(requestCode: Int, resultCode:Int, data: Intent?){
            super.onActivityResult(requestCode,resultCode, data)
            // the user has returned from the settings activity
            if (requestCode == 100) {
                checkPermission()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener {latLng : LatLng ->
            Log.d(TAG,"Long Press at ${latLng.latitude} and ${latLng.longitude}")
            mMap.clear()
            getCinemasNearBy(latLng)
            var myPosition = mMap.addMarker(
                MarkerOptions().position(currLatLng!!).title("YOUR POSITION").icon(
                    bitmapDescriptorFromVector(
                        getApplicationContext(),
                        R.drawable.ic_current_location
                    )
                )
            )
        }
        getCinemasNearBy(currLatLng!!)

        var myPosition = mMap.addMarker(
            MarkerOptions().position(currLatLng!!).title("YOUR POSITION").icon(
                bitmapDescriptorFromVector(
                    getApplicationContext(),
                    R.drawable.ic_current_location
                )
            )
        )

        var previousMarker = myPosition
        mMap.setOnMarkerClickListener { marker ->
            //            val restaurantName = marker.title
            if (previousMarker != marker) {
                marker.tag = true
                previousMarker = marker
                updateConfirmButton(marker,true,myPosition)
            } else {
                var confirmStatus = marker.tag.toString().toBoolean()
                confirmStatus = !confirmStatus
                marker.setTag(confirmStatus)
                if (!confirmStatus){
                }
                updateConfirmButton(marker,confirmStatus,myPosition)
            }
            true
        }
    }

    private fun updateConfirmButton(marker: Marker, confirmStatus: Boolean,myPosition:Marker) {
        // Update the button color -- need to load the color from resources first
        if (confirmStatus){
            marker.showInfoWindow()
            if (!marker.equals(myPosition)){
                val checkIcon = ContextCompat.getDrawable(
                    this, R.drawable.ic_check
                )
                confirm.background = getDrawable(R.drawable.rounded_button)

                // Update the left-aligned icon
                confirm.setCompoundDrawablesWithIntrinsicBounds(checkIcon, null, null, null)

                //Update button text
                confirm.text = "LEARN MORE ABOUT ${marker.title}"
                confirm.isEnabled = true
                currMarker = marker
            } else {
                //note: when select the current location marker, we must set the confirm button as unselected status
                val clearIcon = ContextCompat.getDrawable(
                    this, R.drawable.ic_clear
                )
                confirm.background = getDrawable(R.drawable.rounded_button_unselected)

                // Update the left-aligned icon
                confirm.setCompoundDrawablesWithIntrinsicBounds(clearIcon, null, null, null)

                //Update button text
                confirm.text = "CHOOSE A LOCATION"
                confirm.isEnabled = false
                currMarker = null
            }
        } else {
            marker.hideInfoWindow()
            val clearIcon = ContextCompat.getDrawable(
                this, R.drawable.ic_clear
            )
            confirm.background = getDrawable(R.drawable.rounded_button_unselected)

            // Update the left-aligned icon
            confirm.setCompoundDrawablesWithIntrinsicBounds(clearIcon, null, null, null)

            //Update button text
            confirm.text = "CHOOSE A LOCATION"
            confirm.isEnabled = false
            currMarker =null
        }

    }

    private fun getCinemasNearBy(currLatLng:LatLng){
        doAsync {
            val geocoder = Geocoder(this@MapsActivity)
            doAsync {

                try {
                    val diningManager = CinemaManager()
                    val restaurants = diningManager.retrieveCinemas(
                        LatLng(currLatLng!!.latitude, currLatLng!!.longitude),
                        getString(R.string.yelp_KEY),
                        2000
                    )

                    runOnUiThread {
                        if (restaurants.isEmpty()) {
                            android.util.Log.e("MapsActivity", "no results found")
                        } else {
                            markerList.clear()
                            for (i in restaurants) {
                                val lat = i.cinemaLat
                                val lon = i.cinemaLong
                                doAsync {
                                    val results =
                                        geocoder.getFromLocation(lat, lon, 5)
                                    runOnUiThread {
                                        if (results.isNotEmpty()) {
                                            val cinemaName = i.cinemaName
                                            val cinemaRating = i.cinemaRating

                                            val marker = mMap.addMarker(
                                                MarkerOptions().position(
                                                    LatLng(
                                                        lat,
                                                        lon
                                                    )
                                                )
                                                    .title(cinemaName)
                                                    .snippet(
                                                        "Rating：${String.format(
                                                            "%.0f",
                                                            cinemaRating
                                                        )}"
                                                    )
                                                    .draggable(true).icon(
                                                        bitmapDescriptorFromVector(
                                                            getApplicationContext(),
                                                            R.drawable.ic_cinema_nearby
                                                        )
                                                    )
                                            )

                                            marker.setTag(0)
                                            markerList.put(marker, i.cinemaUrl)
                                        }
                                    }

                                }
                                val zoomLevel = 13.0f
                            }
                        }

                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        e.printStackTrace()
                        // Display some kind of error message
                        Toast.makeText(
                            this@MapsActivity,
                            "Error! No Result",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        vectorResId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}
