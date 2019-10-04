package com.dps.custom_files.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dps.custom_files.R
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.os.Build
import com.dps.custom_files.app_helper.AppConstants.READ_WRITE_PERMISSION_REQUEST_CODE
import androidx.recyclerview.widget.GridLayoutManager
import com.dps.custom_files.adapters.GalleryAdapter
import com.dps.custom_files.databinding.ActivityImagesGalleryBinding


class ImagesGalleryActivity : BaseActivity() {

    private var binding: ActivityImagesGalleryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_images_gallery)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            checkForPermissions(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
        ) {
            requestPermissions(
                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                READ_WRITE_PERMISSION_REQUEST_CODE
            )
        } else
            setRecyclerView()
    }

    private fun setRecyclerView() {
        val width = getDeviceWidth()/2 - 4
        val galleryAdapter = GalleryAdapter(this,fetchAlbums(),width)
        binding?.apply {
            rvAlbums?.layoutManager = GridLayoutManager(this@ImagesGalleryActivity,2)
            rvAlbums.adapter = galleryAdapter
            rvAlbums.setHasFixedSize(true)
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
                if (grantResults.isNotEmpty() && checkForPermissions(grantResults)) {
                    setRecyclerView()
                } else {
                    displayToast("All Permissions Not Granted")
                }
                return
            }
        }
    }
}
