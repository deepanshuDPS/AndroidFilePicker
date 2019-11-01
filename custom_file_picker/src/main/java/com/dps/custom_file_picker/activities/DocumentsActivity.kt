package com.dps.custom_file_picker.activities

import android.os.Build
import android.os.Bundle
import com.dps.custom_file_picker.R
import com.dps.custom_file_picker.app_helper.AppConstants.READ_WRITE_PERMISSION_REQUEST_CODE

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.dps.custom_file_picker.adapters.DocumentsAdapter
import com.dps.custom_file_picker.app_helper.CustomIntent
import com.dps.custom_file_picker.listeners.IsAnyCheckedListener
import com.dps.custom_file_picker.listeners.OnFileSelectedListener
import com.dps.custom_file_picker.models.DocumentModel
import kotlinx.android.synthetic.main.activity_documents.*

class DocumentsActivity : BaseActivity() {

    private var mimeTypes: Array<String>?=null
    private var documentsAdapter: DocumentsAdapter? = null
    private var documentsList: ArrayList<DocumentModel>? = null
    private var count = 0
    private var actionMode: ActionMode? = null
    private var isChecked = false
    private var isSearch = false
    private var multiSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)
        setSupportActionBar(toolbar_documents)

        mimeTypes = intent.getStringArrayExtra(CustomIntent.SELECTED_TYPES)
        val action = intent.action
        multiSelect = (action!=null && action == CustomIntent.ALLOW_MULTIPLE_SELECTION)

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
            if (isSearch) {
                isSearch = false
                invalidateOptionsMenu()
            } else onBackPressed()
        }

        toolbar_documents.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_search -> {
                    isSearch = true
                    invalidateOptionsMenu()
                }
                R.id.menu_clear -> {
                    et_search.setText("")
                }
            }
            return@setOnMenuItemClickListener true
        }
        et_search.addTextChangedListener {
            documentsAdapter?.searchDocs(it.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbar_documents.inflateMenu(R.menu.menu_search)
        val search = menu?.findItem(R.id.menu_search)
        val clear = menu?.findItem(R.id.menu_clear)
        clear?.isVisible = isSearch
        search?.isVisible = !isSearch
        if (isSearch) {
            toolbar_documents.title = ""
            et_search.visibility = View.VISIBLE
        } else {
            toolbar_documents.setTitle(R.string.documents)
            et_search.visibility = View.GONE
        }
        return true
    }


    private fun setRecyclerView() {
        if(mimeTypes?.isNotEmpty()!!){
            documentsList = fetchDocuments(mimeTypes!!)
            documentsAdapter =
                DocumentsAdapter(this, documentsList!!,documentsList!! ,isChecked, object : IsAnyCheckedListener {
                    override fun isAnyChecked(checked: Boolean) {
                        if (checked) count += 1 else count -= 1
                        // show action mode here
                        isChecked = checked
                        if (!isChecked) {
                            for (i in 0 until documentsList!!.size) {
                                if (documentsList!![i].isChecked) {
                                    isChecked = true
                                    break
                                }
                            }
                        }
                        documentsAdapter?.setChecked(isChecked)
                        documentsAdapter?.notifyDataSetChanged()
                        if (isChecked) showActionMode(count)
                        else hideActionMode()
                    }

                }, object : OnFileSelectedListener {
                    override fun onFileSelected(filePath: String) {
                        val selectedFilesList = ArrayList<String>()
                        selectedFilesList.add(filePath)
                        setIntentAndFinish(selectedFilesList)
                    }

                },multiSelect)
            documentsAdapter?.setHasStableIds(true)
            rv_documents.layoutManager = LinearLayoutManager(this)
            rv_documents.adapter = documentsAdapter
        } else{
            displayToast(R.string.no_file_type_mentioned)
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

    private fun hideActionMode() {
        actionMode?.finish()
        actionMode = null
    }

    private fun showActionMode(count: Int) {
        if (actionMode == null)
            actionMode = this@DocumentsActivity.startSupportActionMode(ActionBarCallback())

        actionMode?.title = "$count Selected"
    }

    // action mode for select multiple
    inner class ActionBarCallback : ActionMode.Callback {
        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            // multiple files selected action here
            setIntentAndFinish(getSelectedFiles())
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
            count = 0
            documentsAdapter?.clearAllChecked()
            hideActionMode()
        }
    }

    private fun getSelectedFiles(): ArrayList<String> {
        val selectedFilesList = ArrayList<String>()
        for (i in documentsList!!)
            if (i.isChecked)
                selectedFilesList.add(i.filePath)
        return selectedFilesList
    }
}
