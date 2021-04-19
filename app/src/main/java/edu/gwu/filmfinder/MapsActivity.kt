package edu.gwu.filmfinder

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.core.content.ContextCompat
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

    companion object {
        val TAG = "MapsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        // Triggers the map to load, will call onMapReady when complete
        mapFragment.getMapAsync(this)

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener {latLng : LatLng ->
            Log.d(TAG,"Long Press at ${latLng.latitude} and ${latLng.longitude}")
            mMap.clear()

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
                        mMap.addMarker(marker)
                        updateConfirmButton(firstResult)
                    }
                }
            }
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
