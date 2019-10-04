package com.dps.custom_files.activities

import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dps.custom_files.R
import com.dps.custom_files.adapters.GalleryAdapter
import com.dps.custom_files.adapters.ImagesAdapter
import com.dps.custom_files.databinding.ActivityAlbumsImagesBinding
import com.dps.custom_files.models.ImagesModel

class AlbumsImagesActivity : BaseActivity() {

    private var binding:ActivityAlbumsImagesBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_albums_images)

        setSupportActionBar(binding?.toolbarImages)
        supportActionBar?.title = "Camera"

        val imagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection =
            MediaStore.Images.Media.BUCKET_ID + "='" + intent.getStringExtra("album_id") + "'"
        val orderBy = "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        val albumsImagesCursor = contentResolver.query(
            imagesUri,
            arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_MODIFIED
            ),
            selection,
            null,
            orderBy
        )
        if (albumsImagesCursor != null) {
            val imagesList = ArrayList<ImagesModel>()
            while (albumsImagesCursor.moveToNext()) {
                val imagePath =
                    albumsImagesCursor.getString(albumsImagesCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val imageSize =
                    albumsImagesCursor.getString(albumsImagesCursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                val imageModel = ImagesModel("", imageSize, imagePath)
                imagesList.add(imageModel)
            }
            albumsImagesCursor.close()
            setRecyclerView(imagesList)
        }
    }

    private fun setRecyclerView(imagesList:ArrayList<ImagesModel>) {
        val width = getDeviceWidth()/2 - 4
        val galleryAdapter = ImagesAdapter(this,imagesList,width)
        binding?.apply {
            rvImages?.layoutManager = GridLayoutManager(this@AlbumsImagesActivity,2)
            rvImages.adapter = galleryAdapter
            rvImages.setHasFixedSize(true)
        }
    }
}
