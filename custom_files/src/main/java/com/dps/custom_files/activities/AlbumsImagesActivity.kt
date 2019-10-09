package com.dps.custom_files.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dps.custom_files.R
import com.dps.custom_files.adapters.DatesAdapter
import com.dps.custom_files.app_helper.Utilities
import com.dps.custom_files.databinding.ActivityAlbumsImagesBinding
import com.dps.custom_files.listeners.OnFileSelectedListener
import com.dps.custom_files.listeners.OnMultipleFilesSelectionListener
import com.dps.custom_files.models.ImagesModel
import kotlin.collections.ArrayList


class AlbumsImagesActivity : BaseActivity() {

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

        binding?.toolbarImages?.setNavigationOnClickListener {
            onBackPressed()
        }


        val width = getDeviceWidth() / 3 - 2
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

        var previousDate = "null"
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
        }

    }


    override fun onResume() {
        super.onResume()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val width = getDeviceWidth() / 3 - 2
        datesAdapter = DatesAdapter(
            this,
            allDates!!,
            allImages!!,
            width,
            object : OnMultipleFilesSelectionListener {
                override fun onMultipleFileSelected(selected: Boolean, count: Int) {
                    if (selected)
                        showActionMode(count)
                    else
                        hideActionMode()
                }
            },
            object : OnFileSelectedListener {
                override fun onFileSelected(filePath: String) {
                    val selectedFilesList = ArrayList<String>()
                    selectedFilesList.add(filePath)
                    val intent = Intent()
                    intent.putExtra("files_path",selectedFilesList)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }
            })

        binding?.apply {
            rvImages.adapter = datesAdapter
            val layoutManager = LinearLayoutManager(this@AlbumsImagesActivity)
            rvImages.layoutManager = layoutManager
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
            val intent = Intent()
            intent.putExtra("files_path",getSelectedFiles())
            setResult(Activity.RESULT_OK,intent)
            finish()
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
