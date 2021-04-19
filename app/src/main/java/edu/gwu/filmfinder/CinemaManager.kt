package edu.gwu.filmfinder

import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

class CinemaManager {
    companion object{
        val TAG = "CinemaManager"

    }

    val okHttpClient: OkHttpClient
    init {
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        builder.addInterceptor(logging)
        okHttpClient = builder.build()
    }

    fun retrieveCinemas(
        latLng: LatLng,
        apiKey: String,
        radius: Int
    ): List<Cinemas> {
        // Call the Search API
        val lat = latLng.latitude
        val lon = latLng.longitude

//        https://www.yelp.com/developers/documentation/v3/business_search
        // Create the Request object
        val request = Request.Builder()
            .url("https://api.yelp.com/v3/businesses/search?latitude=$lat&longitude=$lon&radius=$radius&categories=movietheaters&sort_by=distance&limit=10")
            .header(
                "Authorization",
                "Bearer $apiKey"
            )
            .build()

        val response = okHttpClient.newCall(request).execute()
        val responseString: String?  = response.body?.string()
        val cinemas = mutableListOf<Cinemas>()

        if(response.isSuccessful && !responseString.isNullOrEmpty()){
            val jSonObject = JSONObject(responseString)
            val businesses = jSonObject.getJSONArray("businesses")
            for(i in 0 until businesses.length()){
                val cinemaJson = businesses.getJSONObject(i)
                val cinemaName = cinemaJson.getString("name")
                val cinemaRating = cinemaJson.getDouble("rating")
                val cinemaCoordinates = cinemaJson.getJSONObject("coordinates")
                val cinemaLat = cinemaCoordinates.getDouble("latitude")
                val cinemaLong =cinemaCoordinates.getDouble("longitude")
                val cinemaLocation = cinemaJson.getJSONObject("location")
                val cinemaStreet1 = cinemaLocation.getString("address1")
                val cinemaId = cinemaJson.getString("id")
                val cinemaUrl = cinemaJson.getString("url")
                val cinema = Cinemas(
                    cinemaName = cinemaName,
                    cinemaRating = cinemaRating,
                    cinemaLat = cinemaLat,
                    cinemaLong = cinemaLong,
                    cinemaStreet1= cinemaStreet1,
                    cinemaId = cinemaId,
                    cinemaUrl = cinemaUrl
                )
                cinemas.add(cinema)
            }
        }
        return cinemas
    }
}

