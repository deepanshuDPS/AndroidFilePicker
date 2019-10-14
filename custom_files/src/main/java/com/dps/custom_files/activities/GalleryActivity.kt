package com.dps.custom_files.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dps.custom_files.R
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.os.Build
import com.dps.custom_files.app_helper.AppConstants.READ_WRITE_PERMISSION_REQUEST_CODE
import androidx.recyclerview.widget.GridLayoutManager
import com.dps.custom_files.adapters.GalleryAdapter
import com.dps.custom_files.app_helper.AppConstants
import com.dps.custom_files.app_helper.AppConstants.GALLERY_IMAGES_REQUEST_CODE
import com.dps.custom_files.app_helper.CustomIntent
import com.dps.custom_files.databinding.ActivityGalleryBinding
import com.dps.custom_files.listeners.OnAlbumClickListener


class GalleryActivity : BaseActivity() {

    private var binding: ActivityGalleryBinding? = null
    private var multiSelect = false
    private var typeOfChoice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)

        val action = intent.action
        multiSelect = (action != null && action == CustomIntent.ALLOW_MULTIPLE_SELECTION)
        val type = intent.type
        typeOfChoice = (type != null && type == CustomIntent.PICK_IMAGES_ONLY)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkForPermissions(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            )
        ) {
            requestPermissions(
                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                READ_WRITE_PERMISSION_REQUEST_CODE
            )

        } else
            setRecyclerView()

        binding?.toolbarGallery?.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun setRecyclerView() {
        val albums = if (typeOfChoice) fetchImagesAlbums() else fetchGallery()
        val width = getDeviceWidth() / 2 - 4
        val galleryAdapter =
            GalleryAdapter(this, albums, width, object : OnAlbumClickListener {
                override fun onAlbumClick(albumID: String, albumName: String) {
                    val bundle = Bundle()
                    bundle.putString("album_id", albumID)
                    bundle.putString("album_name", albumName)
                    bundle.putBoolean("is_multiple", multiSelect)
                    bundle.putBoolean(AppConstants.ONLY_IMAGES, typeOfChoice)
                    //switchActivity(AlbumsImagesActivity::class.java,bundle)
                    startActivityForResult(
                        Intent(
                            this@GalleryActivity,
                            AlbumsImagesActivity::class.java
                        ).putExtras(bundle), GALLERY_IMAGES_REQUEST_CODE
                    )
                }

            })
        binding?.apply {
            rvAlbums.layoutManager = GridLayoutManager(this@GalleryActivity, 2)
            rvAlbums.adapter = galleryAdapter
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_WRITE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && checkForPermissions(grantResults))
                    setRecyclerView()
                else
                    displayToast(R.string.permissions_not_granted)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_IMAGES_REQUEST_CODE) {
                val intent = Intent()
                intent.putExtra(
                    AppConstants.FILES_PATH,
                    data.extras?.getStringArrayList(AppConstants.FILES_PATH)
                )
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}
