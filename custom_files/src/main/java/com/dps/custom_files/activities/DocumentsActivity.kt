package com.dps.custom_files.activities


import android.os.Build
import android.os.Bundle
import com.dps.custom_files.R
import com.dps.custom_files.app_helper.AppConstants.READ_WRITE_PERMISSION_REQUEST_CODE

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import androidx.recyclerview.widget.LinearLayoutManager
import com.dps.custom_files.adapters.DocumentsAdapter
import kotlinx.android.synthetic.main.activity_documents.*

class DocumentsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkForPermissions(
                WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE
            )
        ) {
            requestPermissions(
                arrayOf(
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE
                ), READ_WRITE_PERMISSION_REQUEST_CODE
            )
        } else
            setRecyclerView()

        toolbar_documents.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerView() {
        val documentsAdapter = DocumentsAdapter(this,fetchDocuments())
        rv_documents.layoutManager = LinearLayoutManager(this)
        rv_documents.adapter = documentsAdapter
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
}
