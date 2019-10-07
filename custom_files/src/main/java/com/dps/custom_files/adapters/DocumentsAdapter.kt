package com.dps.custom_files.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_files.R
import com.dps.custom_files.databinding.ItemDocumentBinding
import com.dps.custom_files.models.DocumentModel

class DocumentsAdapter(private var context: Context,private var documentsList:ArrayList<DocumentModel>) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_document,parent,false) as ItemDocumentBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = documentsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.document = documentsList[position]
    }

    inner class ViewHolder(var binding:ItemDocumentBinding): RecyclerView.ViewHolder(binding.root) {

    }
}