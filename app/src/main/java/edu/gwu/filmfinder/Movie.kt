package edu.gwu.filmfinder

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
){
    constructor():this("","","","","","")
}
