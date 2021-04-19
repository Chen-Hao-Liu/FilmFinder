package edu.gwu.filmfinder

import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MovieManagerDetail (val currentMovie : Movie){ val okHttpClient: OkHttpClient

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
        api_key:String,
        authorization:String,
        language:String
    ): Movie {
        val currentMovieId = currentMovie.id
        val request= Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$currentMovieId?api_key=$api_key&language=$language")
            .headers(Headers.headersOf("Authorization",authorization,"Content-Type",contentType))
            .build()

        val response = okHttpClient.newCall(request).execute()
        val moviesString: String? = response.body?.string()

        if (response.isSuccessful && !moviesString.isNullOrEmpty()){
            val jsonObject = JSONObject(moviesString)

            currentMovie.description = jsonObject.getString("overview")

            currentMovie.rating = jsonObject.getString("vote_average")

        }
        return currentMovie
    }
}
