package com.dps.customfiles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dps.custom_files.activities.DocumentsActivity
import com.dps.custom_files.activities.GalleryActivity
import com.dps.custom_files.activities.MusicsActivity
import com.dps.custom_files.app_helper.AppConstants
import com.dps.custom_files.app_helper.CustomIntent
import com.dps.custom_files.app_helper.MimeTypes

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onGalleryClick(view: View) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.action = CustomIntent.ALLOW_MULTIPLE_SELECTION
        startActivityForResult(intent, 101)
    }

    fun onImgGalleryClick(view: View) {
        val intent = Intent(this, GalleryActivity::class.java)
        intent.action = CustomIntent.ALLOW_MULTIPLE_SELECTION
        intent.type = CustomIntent.PICK_IMAGES_ONLY
        startActivityForResult(intent, 202)
    }

    fun onDocClick(view: View) {
        val intent = Intent(this, DocumentsActivity::class.java)
        intent.action = CustomIntent.ALLOW_MULTIPLE_SELECTION
        intent.putExtra(CustomIntent.SELECTED_TYPES,arrayOf(MimeTypes.PDF,MimeTypes.IMAGE_PNG))
        startActivityForResult(intent, 303)
    }

    fun onMusicClick(view: View) {
        val intent = Intent(this, MusicsActivity::class.java)
        intent.action = CustomIntent.ALLOW_MULTIPLE_SELECTION
        startActivityForResult(intent, 404)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this@MainActivity,"Files Selected: "+data.getStringArrayListExtra(
                AppConstants.FILES_PATH).size,Toast.LENGTH_LONG).show()
        }
    }


}
