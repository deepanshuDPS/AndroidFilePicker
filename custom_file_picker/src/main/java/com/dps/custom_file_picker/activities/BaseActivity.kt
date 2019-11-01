package com.dps.custom_file_picker.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.dps.custom_file_picker.R
import com.dps.custom_file_picker.models.AlbumModel
import java.util.*
import kotlin.collections.ArrayList
import com.bumptech.glide.Glide
import java.io.File
import android.widget.LinearLayout
import com.dps.custom_file_picker.app_helper.AppConstants
import com.dps.custom_file_picker.app_helper.AppConstants.FileType
import com.dps.custom_file_picker.app_helper.MimeTypes
import com.dps.custom_file_picker.app_helper.Utilities
import com.dps.custom_file_picker.models.DocumentModel

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

    /*fun switchActivity(destinationActivity: Class<*>) {
        startActivity(Intent(this, destinationActivity))
    }

    fun switchActivity(destinationActivity: Class<*>, bundle: Bundle?) {
        if (bundle != null)
            startActivity(Intent(this, destinationActivity).putExtras(bundle))
        else
            switchActivity(destinationActivity)
    }*/

    private fun displayToast(message: String) {
        Toast.makeText(this@BaseActivity, message, Toast.LENGTH_LONG).show()
    }

    fun displayToast(stringRes: Int) {
        displayToast(getString(stringRes))
    }

    fun setIntentAndFinish(selectedFilesList:ArrayList<String>){
        val intent = Intent()
        intent.putExtra(AppConstants.FILES_PATH, selectedFilesList)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        @BindingAdapter(value = ["path", "imageSize", "fileType"], requireAll = false)
        @JvmStatic
        fun setBitmapToImageView(
            imageView: ImageView,
            path: String,
            imageSize: Int,
            fileType: FileType
        ) {
            /*val bitmap  = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path,bitmapOptions), thumbSize, thumbSize)
            imageView.setImageBitmap(bitmap)*/
            val file = File(path)
            if (fileType == FileType.IMAGE)
                Glide.with(imageView).asBitmap().load(file).placeholder(R.drawable.bg_default_image)
                    .override(imageSize, imageSize).into(imageView)
            else
                Glide.with(imageView).asBitmap().load((Uri.fromFile(file))).placeholder(R.drawable.bg_default_image)
                    .override(imageSize, imageSize).into(imageView)


        }

        @BindingAdapter("setAlbumIcon")
        @JvmStatic
        fun setAlbumIcon(imageView: ImageView, albumName: String) {
            when {
                albumName.toLowerCase(Locale.ENGLISH).contains("camera") -> imageView.setBackgroundResource(R.drawable.ic_camera)
                albumName.toLowerCase(Locale.ENGLISH).contains("video") -> imageView.setBackgroundResource(R.drawable.ic_video_cam)
                else -> imageView.setBackgroundResource(R.drawable.ic_folder)
            }
        }

        @BindingAdapter("setFileIcon")
        @JvmStatic
        fun setFileIcon(imageView: ImageView, mimeType: String) {

            when (mimeType) {
                MimeTypes.PDF -> imageView.setImageResource(R.drawable.ic_pdf)
                MimeTypes.DOC, MimeTypes.DOC_X -> imageView.setImageResource(
                    R.drawable.ic_doc
                )
                MimeTypes.TXT -> imageView.setImageResource(R.drawable.ic_txt)
                MimeTypes.PPT, MimeTypes.PPT_X -> imageView.setImageResource(
                    R.drawable.ic_ppt
                )
                MimeTypes.XLS, MimeTypes.XLS_X -> imageView.setImageResource(
                    R.drawable.ic_xls
                )
                MimeTypes.HTML -> imageView.setImageResource(R.drawable.ic_html)
                MimeTypes.IMAGE_PNG -> imageView.setImageResource(R.drawable.ic_png)
                MimeTypes.IMAGE_JPEG -> imageView.setImageResource(R.drawable.ic_jpg)
                MimeTypes.IMAGE_GIF -> imageView.setImageResource(R.drawable.ic_gif)
                MimeTypes.IMAGE_BMP -> imageView.setImageResource(R.drawable.ic_bmp)
                MimeTypes.IMAGE_SVG -> imageView.setImageResource(R.drawable.ic_svg)
                MimeTypes.AUDIO_MP3, MimeTypes.AUDIO_OGG, MimeTypes.AUDIO_WAV -> imageView.setImageResource(
                    R.drawable.ic_music
                )
                MimeTypes.VIDEO_MOV, MimeTypes.VIDEO_AVI, MimeTypes.VIDEO_3GP, MimeTypes.VIDEO_MP4, MimeTypes.VIDEO_MPEG -> imageView.setImageResource(
                    R.drawable.ic_video
                )
                else -> imageView.setImageResource(R.drawable.ic_unknown)
            }

        }

        @BindingAdapter("setBackgroundToLayout")
        @JvmStatic
        fun setBackground(linearLayout: LinearLayout, isChecked: Boolean) {
            if (isChecked)
                linearLayout.setBackgroundResource(R.drawable.bg_grey_ripple)
            else
                linearLayout.setBackgroundResource(R.drawable.bg_white_ripple)

        }
    }

    fun fetchImagesAlbums(): ArrayList<AlbumModel> {

        val width = getDeviceWidth() / 2 - 4
        val albumsList = ArrayList<AlbumModel>()
        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val countColumnName = "count"
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
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
                Log.d("Album", "Id $bucketId Name $bucketName Count $count")
                albumsList.add(AlbumModel(bucketId, bucketName, lastFilePath, count,width, FileType.IMAGE))
            }
            cursor.close()
        }
        return albumsList
    }

    fun fetchDocuments(mimeTypeList: Array<String>): ArrayList<DocumentModel> {
        val documentList = ArrayList<DocumentModel>()

        /*val pdf = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")*/

        var nArgs = "("
        for (i in mimeTypeList)
            nArgs += "?,"
        nArgs = nArgs.substring(0, nArgs.length - 1) + ") "
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

        val filesCursor =
            contentResolver.query(filesUri, projection, selection, mimeTypeList, orderBy)

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
                            mimeType, false
                        )
                    )
                else
                    documentList.add(
                        DocumentModel(
                            fileName,
                            filePath,
                            fileSize,
                            Utilities.getSlashedDateFromData(timeInMillis),
                            mimeType, false
                        )
                    )
            }
            filesCursor.close()
        }
        return documentList
    }

    fun fetchGallery():ArrayList<AlbumModel>{

        val width = getDeviceWidth() / 2 - 4
        val allAlbums = ArrayList<AlbumModel>()
        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val countColumnName = "count"
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.BUCKET_ID,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME
        )
        val bucketGroupBy =
            "1) GROUP BY ${MediaStore.Images.ImageColumns.BUCKET_ID}, (${MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME}"
        val bucketOrderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val cursor =
            contentResolver.query(imagesUri, projection, bucketGroupBy, null, bucketOrderBy)

        if(cursor!=null){
            while (cursor.moveToNext()){
                Log.d("bucket_id"," ${cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))}")
                val albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))
                val albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))
                val selection = MediaStore.Images.Media.BUCKET_ID + "='" + albumId + "'"
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
                allAlbums.add(AlbumModel(albumId,albumName,lastFilePath,count,width,FileType.IMAGE))
            }
            cursor.close()
        }

        val imagesAlbumSize = allAlbums.size

        val videosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projectionV = arrayOf(
            MediaStore.Video.VideoColumns.BUCKET_ID,
            MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME
        )
        val bucketGroupByV =
            "1) GROUP BY ${MediaStore.Video.VideoColumns.BUCKET_ID}, (${MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME}"
        val bucketOrderByV = "${MediaStore.Video.VideoColumns.DATE_MODIFIED} DESC"
        val cursorV =
            contentResolver.query(videosUri, projectionV, bucketGroupByV, null, bucketOrderByV)

        if(cursorV!=null){
            while (cursorV.moveToNext()){
                Log.d("bucket_id"," ${cursorV.getString(cursorV.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_ID))}")
                val albumId = cursorV.getString(cursorV.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_ID))
                val albumName = cursorV.getString(cursorV.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME))
                val selection = MediaStore.Video.Media.BUCKET_ID + "='" + albumId + "'"
                val orderBy = "${MediaStore.Video.Media.DATE_MODIFIED} DESC LIMIT 1"
                val latestVideoCursor = contentResolver.query(
                    videosUri,
                    arrayOf(MediaStore.Video.Media.DATA),
                    selection,
                    null,
                    orderBy
                )
                var lastFilePath = ""
                if (latestVideoCursor != null) {
                    latestVideoCursor.moveToFirst()
                    lastFilePath = latestVideoCursor.getString(
                        latestVideoCursor.getColumnIndex(
                            MediaStore.Images.Media.DATA
                        )
                    )
                    latestVideoCursor.close()
                }
                val countCursor = contentResolver.query(
                    videosUri,
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
                var flag = true
                for(i in 0 until imagesAlbumSize){
                    if(allAlbums[i].albumId == albumId){
                        //exist
                        allAlbums[i].fileCount+=count
                        flag = false
                        break
                    }
                }
                if(flag) allAlbums.add(AlbumModel(albumId,albumName,lastFilePath,count,width,FileType.VIDEO))


            }
            cursorV.close()
        }
        /*for(i in allAlbums){
            Log.d("album_details","Name: ${i.albumName} Id:${i.albumId} Count:${i.fileCount} Path:${i.lastFilePath}")
        }*/
        return allAlbums
    }

}