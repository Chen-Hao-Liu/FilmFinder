package edu.gwu.filmfinder

class Util {
    companion object{
        fun isStringEmpty(str:String):Boolean{
            return (str == null || str.length == 0)
        }
    }
}
