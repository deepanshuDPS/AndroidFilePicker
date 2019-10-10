package com.dps.custom_files.activities

import android.content.Intent
import android.content.pm.PackageManager
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
import com.bumptech.glide.Glide
import java.io.File
import android.widget.LinearLayout
import com.dps.custom_files.app_helper.MimeTypes
import com.dps.custom_files.app_helper.Utilities
import com.dps.custom_files.models.DocumentModel

abstract class BaseActivity : AppCompatActivity() {

    private val displayMetrics = DisplayMetrics()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        windowManager.defaultDisplay.getMetrics(displayMetrics)

    }


    fun checkForPermissions(vararg permissions: String): Boolean {
        var permissionGranted = true
        for (i in permissions)
            permissionGranted = permissionGranted && ContextCompat.checkSelfPermission(
                this,
                i
            ) != PackageManager.PERMISSION_GRANTED
        return permissionGranted
    }

    fun getDeviceWidth() = displayMetrics.widthPixels
    //fun getDeviceHeight() = displayMetrics.heightPixels

    fun checkForPermissions(permissions: IntArray): Boolean {
        var permissionGranted = true
        for (i in permissions)
            permissionGranted = permissionGranted && i == PackageManager.PERMISSION_GRANTED
        return permissionGranted
    }

    fun switchActivity(destinationActivity: Class<*>) {
        startActivity(Intent(this, destinationActivity))
    }

    fun switchActivity(destinationActivity: Class<*>, bundle: Bundle?) {
        if (bundle != null)
            startActivity(Intent(this, destinationActivity).putExtras(bundle))
        else
            switchActivity(destinationActivity)
    }

    fun displayToast(message: String) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_LONG).show()
    }

    fun displayToast(stringRes: Int) {
        displayToast(getString(stringRes))
    }


    companion object {
        @BindingAdapter(value = ["path", "imageSize"], requireAll = false)
        @JvmStatic
        fun setBitmapToImageView(imageView: ImageView, path: String, imageSize: Int) {
            /*val bitmap  = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path,bitmapOptions), thumbSize, thumbSize)
            imageView.setImageBitmap(bitmap)*/
            val file = File(path)
            Glide.with(imageView).asBitmap().load(file).placeholder(R.drawable.bg_default_image)
                .override(imageSize, imageSize).into(imageView)

        }

        @BindingAdapter("setAlbumIcon")
        @JvmStatic
        fun setAlbumIcon(imageView: ImageView, albumName: String) {
            if (albumName.toLowerCase(Locale.ENGLISH).contains("camera"))
                imageView.setBackgroundResource(R.drawable.ic_camera)
            else imageView.setBackgroundResource(R.drawable.ic_folder)
        }

        @BindingAdapter("setFileIcon")
        @JvmStatic
        fun setFileIcon(imageView: ImageView, mimeType: String) {

            when (mimeType) {
                MimeTypes.PDF -> imageView.setImageResource(R.drawable.ic_pdf)
                MimeTypes.DOC,MimeTypes.DOC_X -> imageView.setImageResource(
                    R.drawable.ic_doc
                )
                MimeTypes.TXT -> imageView.setImageResource(R.drawable.ic_txt)
                MimeTypes.PPT,MimeTypes.PPT_X -> imageView.setImageResource(
                    R.drawable.ic_ppt
                )
                MimeTypes.XLS,MimeTypes.XLS_X -> imageView.setImageResource(
                    R.drawable.ic_xls
                )
                MimeTypes.HTML -> imageView.setImageResource(R.drawable.ic_html)
                MimeTypes.IMAGE_PNG -> imageView.setImageResource(R.drawable.ic_png)
                MimeTypes.IMAGE_JPEG -> imageView.setImageResource(R.drawable.ic_jpg)
                MimeTypes.IMAGE_GIF -> imageView.setImageResource(R.drawable.ic_gif)
                MimeTypes.IMAGE_BMP-> imageView.setImageResource(R.drawable.ic_bmp)
                MimeTypes.IMAGE_SVG -> imageView.setImageResource(R.drawable.ic_svg)
                MimeTypes.AUDIO_MP3,MimeTypes.AUDIO_OGG,MimeTypes.AUDIO_WAV -> imageView.setImageResource(R.drawable.ic_music)
                MimeTypes.VIDEO_MOV,MimeTypes.VIDEO_AVI,MimeTypes.VIDEO_3GP,MimeTypes.VIDEO_MP4,MimeTypes.VIDEO_MPEG -> imageView.setImageResource(R.drawable.ic_video)
                else -> imageView.setImageResource(R.drawable.ic_unknown)
            }

        }

        @BindingAdapter("setBackgroundToLayout")
        @JvmStatic
        fun setBackground(linearLayout: LinearLayout, isChecked:Boolean) {
            if(isChecked)
                linearLayout.setBackgroundResource(R.drawable.bg_grey_ripple)
            else
                linearLayout.setBackgroundResource(R.drawable.bg_white_ripple)

        }
    }

    fun fetchImagesAlbums(): ArrayList<AlbumModel> {
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
                val latestImageCursor = contentResolver.query(
                    imagesUri,
                    arrayOf(MediaStore.Images.Media.DATA),
                    selection,
                    null,
                    orderBy
                )
                var lastFilePath = ""
                if (latestImageCursor != null) {
                    latestImageCursor.moveToFirst()
                    lastFilePath = latestImageCursor.getString(
                        latestImageCursor.getColumnIndex(
                            MediaStore.Images.Media.DATA
                        )
                    )
                    latestImageCursor.close()
                }
                val countCursor = contentResolver.query(
                    imagesUri,
                    arrayOf("$countColumnName(*) AS $countColumnName"),
                    selection,
                    null,
                    null
                )
                var count = 0
                if (countCursor != null) {
                    countCursor.moveToFirst()
                    count = countCursor.getInt(countCursor.getColumnIndexOrThrow(countColumnName))
                    countCursor.close()
                }
                val albumModel = AlbumModel(
                    bucketId,
                    bucketName,
                    lastFilePath,
                    count.toString(),
                    getDeviceWidth() / 2 - 4
                )
                Log.d("Album", "Id $bucketId Name $bucketName Count $count")
                albumsList.add(albumModel)
            }
            cursor.close()
        }
        return albumsList
    }


    fun fetchDocuments(mimeTypeList:Array<String>): ArrayList<DocumentModel> {
        val documentList = ArrayList<DocumentModel>()

        /*val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")*/

        var nArgs = "("
        for(i in mimeTypeList)
            nArgs+="?,"
        nArgs = nArgs.substring(0,nArgs.length-1) + ") "
        val filesUri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_MODIFIED,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection = MediaStore.Files.FileColumns.MIME_TYPE + " IN $nArgs "

        val orderBy = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"
       // val args = arrayOf(pdf, doc, docx, xls, xlsx, ppt, pptx, txt, html)

        val filesCursor = contentResolver.query(filesUri, projection, selection, mimeTypeList, orderBy)

        if (filesCursor != null) {
            while (filesCursor.moveToNext()) {
                val filePath =
                    filesCursor.getString(filesCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                val fileSize = Utilities.getFileSize(
                    filesCursor.getString(
                        filesCursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
                    )
                )
                val timeInMillis =
                    filesCursor.getString(filesCursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED)).toLong() * 1000
                val mimeType =
                    filesCursor.getString(filesCursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
                if (Utilities.getTodayDate() == Utilities.getSlashedDateFromData(timeInMillis))
                    documentList.add(
                        DocumentModel(
                            fileName,
                            filePath,
                            fileSize,
                            Utilities.getTimeFromData(timeInMillis),
                            mimeType,false
                        )
                    )
                else
                    documentList.add(
                        DocumentModel(
                            fileName,
                            filePath,
                            fileSize,
                            Utilities.getSlashedDateFromData(timeInMillis),
                            mimeType,false
                        )
                    )
            }
            filesCursor.close()
        }
        return documentList
    }

}