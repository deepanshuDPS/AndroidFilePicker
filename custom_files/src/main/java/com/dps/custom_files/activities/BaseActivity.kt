package com.dps.custom_files.activities

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.dps.custom_files.R
import com.dps.custom_files.models.AlbumModel
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Bitmap



abstract class BaseActivity : AppCompatActivity(){

    private val displayMetrics = DisplayMetrics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager.defaultDisplay.getMetrics(displayMetrics)

    }


    fun checkForPermissions(vararg permissions:String):Boolean{
        var permissionGranted = true
        for(i in permissions)
            permissionGranted = permissionGranted && ContextCompat.checkSelfPermission(this, i)!= PackageManager.PERMISSION_GRANTED
        return permissionGranted
    }

    fun getDeviceWidth() = displayMetrics.widthPixels
    fun getDeviceHeight() = displayMetrics.heightPixels

    fun checkForPermissions(permissions:IntArray):Boolean{
        var permissionGranted = true
        for(i in permissions)
            permissionGranted = permissionGranted && i== PackageManager.PERMISSION_GRANTED
        return permissionGranted
    }
    fun displayToast(message:String){
        Toast.makeText(this@BaseActivity,message,Toast.LENGTH_LONG).show()
    }

    fun displayToast(stringRes:Int){
        displayToast(getString(stringRes))
    }


    companion object{

        @BindingAdapter("setBitmapImage")
        @JvmStatic
        fun setBitmapToImageView(imageView:ImageView,path:String){

            /*val stream = ByteArrayOutputStream()
            // Compress the bitmap with JPEG format and quality 20%
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,stream)
            val byteArray = stream.toByteArray()
            val compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)*/
            val bitmapOptions = BitmapFactory.Options()
            bitmapOptions.inSampleSize = 8
            val thumbSize = 128
            val bitmap  = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path,bitmapOptions), thumbSize, thumbSize)
            imageView.setImageBitmap(bitmap)

        }

        @BindingAdapter("setAlbumIcon")
        @JvmStatic
        fun setAlbumIcon(imageView: ImageView,albumName:String){
            when(albumName.toLowerCase(Locale.ENGLISH)){
                "camera"-> imageView.setBackgroundResource(R.drawable.ic_camera)
                else -> imageView.setBackgroundResource(R.drawable.ic_folder)
            }
        }
    }


    fun fetchAlbums() : ArrayList<AlbumModel> {
        val albumsList = ArrayList<AlbumModel>()
        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val countColumnName = "count"
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.DATE_TAKEN
        )
        val bucketGroupBy =
            "1) GROUP BY ${MediaStore.Images.ImageColumns.BUCKET_ID}, (${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME}"
        val bucketOrderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val cursor =
            contentResolver.query(imagesUri, projection, bucketGroupBy, null, bucketOrderBy)
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val bucketId =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))
                val bucketName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                val selection = MediaStore.Images.Media.BUCKET_ID + "='" + bucketId + "'"
                val orderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT 1"
                val latestImageCursor = contentResolver.query(imagesUri, arrayOf(MediaStore.Images.Media.DATA),selection,null,orderBy)
                var lastFilePath = ""
                if(latestImageCursor!=null){
                    latestImageCursor.moveToFirst()
                    lastFilePath = latestImageCursor.getString(latestImageCursor.getColumnIndex(
                        MediaStore.Images.Media.DATA))
                    latestImageCursor.close()
                }
                val countCursor = contentResolver.query(imagesUri,arrayOf("$countColumnName(*) AS $countColumnName"),selection,null,null)
                var count = 0
                if(countCursor!=null){
                    countCursor.moveToFirst()
                    count = countCursor.getInt(countCursor.getColumnIndexOrThrow(countColumnName))
                    countCursor.close()
                }
                val albumModel = AlbumModel(bucketId,bucketName,lastFilePath,count.toString())
                Log.d("Album","Id $bucketId Name $bucketName Count $count")
                albumsList.add(albumModel)
            }
            cursor.close()
        }
        return albumsList
    }

}