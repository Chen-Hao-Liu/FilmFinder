package edu.gwu.filmfinder

import android.content.Context
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MovieManagerComingSoon  {
    val okHttpClient: OkHttpClient

    init{
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
        builder.addInterceptor(logging)
        okHttpClient = builder.build()
    }

    fun retrieveMovie(
        contentType:String,
        primaryReleaseDateGte:String,
        primaryReleaseDateLte:String,
        api_key:String,
        authorization:String,
        language:String,
        imaFrontPAth:String,
        n: String
    ):List<Movie> {
        val request=Request.Builder()
            .url("https://api.themoviedb.org/3/discover/movie?api_key=$api_key&primary_release_date.gte=$primaryReleaseDateGte&primary_release_date.lte=$primaryReleaseDateLte&language=$language")
            .headers(Headers.headersOf("Authorization",authorization,"Content-Type",contentType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val moviesString: String? = response.body?.string()
        val movieList = mutableListOf<Movie>()

        if (response.isSuccessful && !moviesString.isNullOrEmpty()){
            val jsonObject = JSONObject(moviesString)
            val films = jsonObject.getJSONArray("results")
            for (i in 0 until films.length()){
                val filmJson = films.getJSONObject(i)
                val filmId = filmJson.getString("id")
                val filmName = filmJson.getString("title")
                val imageUrl = imaFrontPAth+filmJson.getString("poster_path")
                val releaseDate = filmJson.getString("release_date")

                val movie = Movie(
                    filmName,
                    filmId,
                    imageUrl,
                    releaseDate,
                    "No Description!",
                    "No Rating Result")
                movieList.add(movie)
            }

        }
        return movieList
    }
}
