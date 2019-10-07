package com.dps.custom_files.app_helper

import java.text.SimpleDateFormat
import java.util.*

object Utilities {

    fun getDateFromData(timeInMillis:Long):String{
        val simple = SimpleDateFormat("dd MMM, yyyy", Locale.UK)
        simple.timeZone = TimeZone.getDefault()
        val result = Date(timeInMillis)
        return  simple.format(result)
    }

    fun getTodayDate():String{
        val simple = SimpleDateFormat("dd/MM/yy", Locale.UK)
        simple.timeZone = TimeZone.getDefault()
        val result = Date(System.currentTimeMillis())
        return  simple.format(result)
    }

    fun getCurrentYear():String{
        val simple = SimpleDateFormat("yyyy", Locale.UK)
        simple.timeZone = TimeZone.getDefault()
        val result = Date(System.currentTimeMillis())
        return  simple.format(result)
    }

    fun getSlashedDateFromData(timeInMillis:Long):String{
        val simple = SimpleDateFormat("dd/MM/yy", Locale.UK)
        simple.timeZone = TimeZone.getDefault()
        val result = Date(timeInMillis)
        return  simple.format(result)
    }

    fun getTimeFromData(timeInMillis:Long):String{
        val simple = SimpleDateFormat("h:mm a", Locale.UK)
        simple.timeZone = TimeZone.getDefault()
        val result = Date(timeInMillis)
        return  simple.format(result)
    }

    fun getFileSize(fileSize: String): String {

        val fileSizeInKB = fileSize.toLong() / 1024
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        val fileSizeInMB = fileSizeInKB / 1024

        return when {
            fileSizeInMB > 0 -> "$fileSizeInMB MB"
            fileSizeInKB > 0 -> "$fileSizeInKB kB"
            else -> "Size: $fileSize bytes"
        }

    }

}