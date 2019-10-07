package com.dps.custom_files.activities

import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dps.custom_files.R
import com.dps.custom_files.adapters.DatesAdapter
import com.dps.custom_files.app_helper.Utilities
import com.dps.custom_files.databinding.ActivityAlbumsImagesBinding
import kotlin.collections.ArrayList




class AlbumsImagesActivity : BaseActivity() {

    private var allDates: ArrayList<String>?=null
    private var allImages: LinkedHashMap<String, ArrayList<String>>? =null
    private var binding: ActivityAlbumsImagesBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_albums_images)

        setSupportActionBar(binding?.toolbarImages)
        supportActionBar?.title = intent.extras?.getString("album_name")

        binding?.toolbarImages?.setNavigationOnClickListener {
            onBackPressed()
        }

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
                    allImages!![date]?.add(imagePath)
                } else
                    allImages!![previousDate]?.add(imagePath)

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
        val width = getDeviceWidth() / 4 - 2
        val datesAdapter = DatesAdapter(this,allDates!!,allImages!!, width)
        binding?.apply {
            rvImages.adapter = datesAdapter
            val layoutManager =LinearLayoutManager(this@AlbumsImagesActivity)
            rvImages.layoutManager = layoutManager
        }
    }
}
