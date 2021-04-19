package edu.gwu.filmfinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
@Parcelize

data class Movie(
//    @SerializedName("title")
    var title : String,
//    @SerializedName("id")
    val id : String,
//    @SerializedName("poster_path")
    val imageUrl : String,
//    @SerializedName("release_date")
    val releaseDate : String,
//    @SerializedName("overview")
    var description :String,
//    @SerializedName("vote_average")
    var rating:String
): Parcelable{
    constructor():this("","","","","","")
}
