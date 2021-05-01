package edu.gwu.filmfinder

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val uid : String,
    var username: String,
    var email :String

):Parcelable{
    constructor():this("","","")
}
