package com.dps.custom_files.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_files.R
import com.dps.custom_files.databinding.ItemDocumentBinding
import com.dps.custom_files.listeners.IsAnyCheckedListener
import com.dps.custom_files.listeners.OnFileSelectedListener
import com.dps.custom_files.models.DocumentModel
import java.util.*
import kotlin.collections.ArrayList


class DocumentsAdapter(
    private var context: Context,
    private var documentsList: ArrayList<DocumentModel>,
    private var documentsSearchList: ArrayList<DocumentModel>,
    private var isChecked:Boolean,
    private var isAnyCheckedListener: IsAnyCheckedListener,
    private var onFileSelectedListener: OnFileSelectedListener,
    private var isMultipleAllowed:Boolean
) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_document,
            parent,
            false
        ) as ItemDocumentBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = documentsSearchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.document = documentsSearchList[position]
        holder.bindOnClickListeners(documentsSearchList[position])
    }

    fun clearAllChecked() {
        for (i in documentsSearchList)
            i.isChecked = false
        isChecked = false
        notifyDataSetChanged()
    }

    fun setChecked(checked: Boolean) {
        isChecked = checked
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun searchDocs(searchQuery:String){

        documentsSearchList = ArrayList()
        if(searchQuery != ""){
            for(i in documentsList){
                if(i.fileName.toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase(Locale.ENGLISH))){
                    documentsSearchList.add(i)
                }
            }
        }
        else
            documentsSearchList.addAll(documentsList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding: ItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindOnClickListeners(documentModel: DocumentModel) {

            fun makeCheckable() {
                documentModel.isChecked = !documentModel.isChecked
                isAnyCheckedListener.isAnyChecked(documentModel.isChecked)
            }

            binding.root.setOnLongClickListener {
                if(isMultipleAllowed)
                    makeCheckable()
                return@setOnLongClickListener true
            }

            binding.root.setOnClickListener {

                if(isChecked)
                        makeCheckable()
                else  // one file selected
                    onFileSelectedListener.onFileSelected(documentModel.filePath)

            }
        }

    }
}