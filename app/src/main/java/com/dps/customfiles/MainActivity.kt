package com.dps.customfiles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dps.custom_files.activities.DocumentsActivity
import com.dps.custom_files.activities.ImagesGalleryActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onGalleryClick(view: View) {
        startActivity(Intent(this,ImagesGalleryActivity::class.java))
    }
    fun onDocClick(view: View) {
        startActivity(Intent(this,DocumentsActivity::class.java))
    }
}
