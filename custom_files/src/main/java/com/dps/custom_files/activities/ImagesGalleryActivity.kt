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
import com.dps.custom_files.app_helper.AppConstants.GALLERY_IMAGES_REQUEST_CODE
import com.dps.custom_files.databinding.ActivityImagesGalleryBinding
import com.dps.custom_files.listeners.OnAlbumClickListener


class ImagesGalleryActivity : BaseActivity() {

    private var binding: ActivityImagesGalleryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_images_gallery)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkForPermissions(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
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
        val width = getDeviceWidth() / 2 - 4
        val galleryAdapter =
            GalleryAdapter(this, fetchImagesAlbums(), width, object : OnAlbumClickListener {
                override fun onAlbumClick(albumID: String,albumName:String) {
                    val bundle = Bundle()
                    bundle.putString("album_id",albumID)
                    bundle.putString("album_name",albumName)
                    //switchActivity(AlbumsImagesActivity::class.java,bundle)
                    startActivityForResult(Intent(this@ImagesGalleryActivity,AlbumsImagesActivity::class.java).putExtras(bundle),GALLERY_IMAGES_REQUEST_CODE)
                }

            })
        binding?.apply {
            rvAlbums.layoutManager = GridLayoutManager(this@ImagesGalleryActivity, 2)
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
        if(data!=null && resultCode== Activity.RESULT_OK){
            if(requestCode==GALLERY_IMAGES_REQUEST_CODE){
                val intent = Intent()
                intent.putExtra("files_path", data.extras?.getStringArrayList("files_path"))
                setResult(Activity.RESULT_OK,intent)
                finish()
            }
        }
    }
}
