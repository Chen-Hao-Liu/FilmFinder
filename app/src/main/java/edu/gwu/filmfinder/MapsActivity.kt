package edu.gwu.filmfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var confirm: Button
    private lateinit var currentLocation: ImageButton
    private var currentAddress:Address? = null
    private lateinit var locationProvider: FusedLocationProviderClient

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
            if (currentAddress != null) {
                val url = "https://www.google.com/"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                startActivity(intent)
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
                    val marker = MarkerOptions().position(latLng).title(streetAddress)
                    mMap.clear()
                    mMap.addMarker(marker)
                    updateConfirmButton(firstResult)
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
            doGeoCoding(latLng)
        }
    }

    private fun updateConfirmButton(address: Address) {
        // Update the button color -- need to load the color from resources first
        val checkIcon = ContextCompat.getDrawable(
            this, R.drawable.ic_check
        )
        confirm.background = getDrawable(R.drawable.rounded_button)

        // Update the left-aligned icon
        confirm.setCompoundDrawablesWithIntrinsicBounds(checkIcon, null, null, null)

        //Update button text
        confirm.text = address.getAddressLine(0)
        confirm.isEnabled = true
    }

}
