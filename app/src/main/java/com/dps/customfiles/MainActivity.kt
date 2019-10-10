package com.dps.customfiles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.dps.custom_files.activities.DocumentsActivity
import com.dps.custom_files.activities.ImagesGalleryActivity
import com.dps.custom_files.app_helper.MimeTypes

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onGalleryClick(view: View) {
        val intent = Intent(this, ImagesGalleryActivity::class.java)
        intent.action = Intent.EXTRA_ALLOW_MULTIPLE
        startActivityForResult(intent, 202)
    }

    fun onDocClick(view: View) {
        val intent = Intent(this, DocumentsActivity::class.java)
        intent.action = Intent.EXTRA_ALLOW_MULTIPLE
        intent.putExtra(MimeTypes.SELECTED_TYPES,arrayOf(MimeTypes.PDF,MimeTypes.IMAGE_PNG))
        startActivityForResult(intent, 303)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == 202)
                Toast.makeText(this@MainActivity,"Files Selected: "+data.getStringArrayListExtra("files_path").size,Toast.LENGTH_LONG).show()
            else if(requestCode == 303)
                Toast.makeText(this@MainActivity,"Files Selected: "+data.getStringArrayListExtra("files_path").size,Toast.LENGTH_LONG).show()
        }
    }
}
