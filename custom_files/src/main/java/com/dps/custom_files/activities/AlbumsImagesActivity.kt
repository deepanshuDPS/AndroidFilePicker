package com.dps.custom_files.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dps.custom_files.R
import com.dps.custom_files.adapters.DatesAdapter
import com.dps.custom_files.app_helper.AppConstants
import com.dps.custom_files.app_helper.Utilities
import com.dps.custom_files.app_helper.AppConstants.FileType
import com.dps.custom_files.databinding.ActivityAlbumsImagesBinding
import com.dps.custom_files.listeners.OnFileSelectedListener
import com.dps.custom_files.listeners.OnMultipleFilesSelectionListener
import com.dps.custom_files.models.ImagesModel
import kotlin.collections.ArrayList

@SuppressLint("Recycle")
class AlbumsImagesActivity : BaseActivity() {

    private var multiSelect = false
    private var datesAdapter: DatesAdapter? = null
    private var allDates: ArrayList<String>? = null
    private var allImages: LinkedHashMap<String, ArrayList<ImagesModel>>? = null
    private var binding: ActivityAlbumsImagesBinding? = null
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_albums_images)

        setSupportActionBar(binding?.toolbarImages)
        supportActionBar?.title = intent.extras?.getString("album_name")

        multiSelect = intent.getBooleanExtra("is_multiple", false)

        binding?.toolbarImages?.setNavigationOnClickListener { onBackPressed() }

        if(intent.getBooleanExtra(AppConstants.ONLY_IMAGES,false))
            addImagesOnly(fetchImagesOnly()!!)
        else {
            val imagesCursor = fetchImagesOnly()
            val videosCursor = fetchVideosOnly()
            if (imagesCursor != null && videosCursor != null)
                mergeBoth(imagesCursor, videosCursor)
            else if (imagesCursor != null)
                addImagesOnly(imagesCursor)
            else if (videosCursor != null)
                addVideosOnly(videosCursor)

        }
    }

    private fun addVideosOnly(videosCursor: Cursor) {
        val width = getDeviceWidth() / 3 - 2
        var previousDate = "null"
        allImages = LinkedHashMap()
        allDates = ArrayList()
        while(videosCursor.moveToNext()){
            val timeInMillisVideo: Long =
                videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)).toLong() * 1000
            val videoPath = videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATA))
            val date = Utilities.getDateFromData(timeInMillisVideo)
            val size = allImages?.size
            if (size == 0 || date != previousDate) {
                allDates?.add(date)
                allImages!![date] = ArrayList()
                allImages!![date]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))
            } else
                allImages!![previousDate]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))

            previousDate = date
        }
    }

    private fun addImagesOnly(imagesCursor: Cursor) {
        val width = getDeviceWidth() / 3 - 2
        var previousDate = "null"
        allImages = LinkedHashMap()
        allDates = ArrayList()
        while( imagesCursor.moveToNext()){
            val timeInMillisImage: Long =
                imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)).toLong() * 1000
            val imagePath = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val date = Utilities.getDateFromData(timeInMillisImage)
            val size = allImages?.size
            if (size == 0 || date != previousDate) {
                allDates?.add(date)
                allImages!![date] = ArrayList()
                allImages!![date]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))
            } else
                allImages!![previousDate]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))

            previousDate = date
        }
    }

    private fun mergeBoth(imagesCursor: Cursor, videosCursor: Cursor) {
        val width = getDeviceWidth() / 3 - 2
        var previousDate = "null"
        allImages = LinkedHashMap()
        allDates = ArrayList()

        // merge sorting from here
        var isVideoExist = videosCursor.moveToNext()
        var isImageExist = imagesCursor.moveToNext()
        while (isImageExist && isVideoExist) {
            val timeInMillisImage: Long = imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)).toLong() * 1000
            val timeInMillisVideo: Long = videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)).toLong() * 1000
            if (timeInMillisImage > timeInMillisVideo) {
                //insert image
                val imagePath =
                    imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val date = Utilities.getDateFromData(timeInMillisImage)
                val size = allImages?.size
                if (size == 0 || date != previousDate) {
                    allDates?.add(date)
                    allImages!![date] = ArrayList()
                    allImages!![date]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))
                } else
                    allImages!![previousDate]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))

                previousDate = date
                isImageExist = imagesCursor.moveToNext()

            } else {
                //insert video
                val videoPath =
                    videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val date = Utilities.getDateFromData(timeInMillisVideo)
                val size = allImages?.size
                if (size == 0 || date != previousDate) {
                    allDates?.add(date)
                    allImages!![date] = ArrayList()
                    allImages!![date]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))
                } else
                    allImages!![previousDate]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))

                previousDate = date
                isVideoExist = videosCursor.moveToNext()

            }
        }

        while (isImageExist) {
            //insert image
            val timeInMillisImage: Long =
                imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)).toLong() * 1000
            val imagePath =
                imagesCursor.getString(imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA))
            val date = Utilities.getDateFromData(timeInMillisImage)
            val size = allImages?.size
            if (size == 0 || date != previousDate) {
                allDates?.add(date)
                allImages!![date] = ArrayList()
                allImages!![date]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))
            } else
                allImages!![previousDate]?.add(ImagesModel(width, imagePath, false,FileType.IMAGE))

            previousDate = date
            isImageExist = imagesCursor.moveToNext()
        }

        while (isVideoExist) {
            //insert video
            val timeInMillisVideo: Long =
                videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)).toLong() * 1000
            val videoPath =
                videosCursor.getString(videosCursor.getColumnIndex(MediaStore.Video.Media.DATA))
            val date = Utilities.getDateFromData(timeInMillisVideo)
            val size = allImages?.size
            if (size == 0 || date != previousDate) {
                allDates?.add(date)
                allImages!![date] = ArrayList()
                allImages!![date]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))
            } else
                allImages!![previousDate]?.add(ImagesModel(width, videoPath, false,FileType.VIDEO))
            previousDate = date
            isVideoExist = videosCursor.moveToNext()
        }

        imagesCursor.close()
        videosCursor.close()
    }

    private fun fetchVideosOnly(): Cursor? {
        val videosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val selection =
            MediaStore.Video.Media.BUCKET_ID + "='${intent.extras?.getString("album_id")}'"
        val orderBy = "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        val albumsVideosCursor = contentResolver.query(
            videosUri,
            arrayOf(
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_MODIFIED
            ),
            selection,
            null,
            orderBy
        )

        return if (albumsVideosCursor?.count == 0) null
        else albumsVideosCursor
    }

    private fun fetchImagesOnly(): Cursor? {

        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection =
            MediaStore.Images.Media.BUCKET_ID + "='${intent.extras?.getString("album_id")}'"
        val orderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val albumsImagesCursor = contentResolver.query(
            imagesUri,
            arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED
            ),
            selection,
            null,
            orderBy
        )
        return if (albumsImagesCursor?.count == 0) null
        else albumsImagesCursor

        /*var previousDate = "null"
        allImages = LinkedHashMap()
        allDates = ArrayList()

        if (albumsImagesCursor != null) {
            while (albumsImagesCursor.moveToNext()) {

                val timeInMillis: Long = albumsImagesCursor.getString(
                    albumsImagesCursor.getColumnIndex(
                        MediaStore.Images.Media.DATE_MODIFIED
                    )
                ).toLong() * 1000
                val imagePath =
                    albumsImagesCursor.getString(albumsImagesCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val date = Utilities.getDateFromData(timeInMillis)
                val size = allImages?.size
                if (size == 0 || date != previousDate) {
                    allDates?.add(date)
                    allImages!![date] = ArrayList()
                    allImages!![date]?.add(ImagesModel(width, imagePath, false))
                } else
                    allImages!![previousDate]?.add(ImagesModel(width, imagePath, false))

                previousDate = date
            }
            albumsImagesCursor.close()
        }*/
    }


    override fun onResume() {
        super.onResume()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val width = getDeviceWidth() / 3 - 2
        datesAdapter = DatesAdapter(
            this@AlbumsImagesActivity,
            allDates!!,
            allImages!!,
            width,
            object : OnMultipleFilesSelectionListener {
                override fun onMultipleFileSelected(selected: Boolean, count: Int) {
                    if (selected) showActionMode(count)
                    else hideActionMode()
                }
            },
            object : OnFileSelectedListener {
                override fun onFileSelected(filePath: String) {
                    val selectedFilesList = ArrayList<String>()
                    selectedFilesList.add(filePath)
                    setIntentAndFinish(selectedFilesList)
                }
            }, multiSelect
        )

        binding?.apply {
            datesAdapter?.setHasStableIds(true)
            rvImagesDate.adapter = datesAdapter
            val layoutManager = LinearLayoutManager(this@AlbumsImagesActivity)
            rvImagesDate.layoutManager = layoutManager
            val animator = rvImagesDate.getItemAnimator()
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
        }
    }

    private fun hideActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    private fun showActionMode(count: Int) {
        if (actionMode == null)
            actionMode = this@AlbumsImagesActivity.startSupportActionMode(ActionBarCallback())

        actionMode?.title = "$count Selected"
    }

    // action mode for select multiple
    inner class ActionBarCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            // multiple files selected action here
            setIntentAndFinish(getSelectedFiles())
            return true
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode?.menuInflater?.inflate(R.menu.menu_mulitple_select, menu)

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            mode?.finish()
            datesAdapter?.clearAllChecked()
            hideActionMode()
        }
    }

    private fun getSelectedFiles(): ArrayList<String> {

        val hmIterator = allImages?.entries?.iterator()
        val selectedFilesList = ArrayList<String>()
        while (hmIterator!!.hasNext()) {
            val entry = hmIterator.next()
            val images = entry.value
            for (i in 0 until images.size) {
                val image = images[i]
                if (image.isChecked)
                    selectedFilesList.add(image.imagePath)

            }
        }
        return selectedFilesList
    }

}
