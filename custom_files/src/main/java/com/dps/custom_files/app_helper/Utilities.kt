package com.dps.custom_files.app_helper

import java.text.SimpleDateFormat
import java.util.*

object Utilities {

    fun getDateFromData(timeInMillis:Long):String{
        val simple = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val result = Date(timeInMillis)
        return  simple.format(result)
    }

}