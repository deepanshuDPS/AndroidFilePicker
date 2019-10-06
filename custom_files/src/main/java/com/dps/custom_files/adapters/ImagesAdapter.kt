package com.dps.custom_files.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_files.R
import com.dps.custom_files.databinding.ItemDatesBinding
import com.dps.custom_files.databinding.ItemImagesBinding

class ImagesAdapter(private var context: Context, private var imagesList:ArrayList<String>, private var width:Int): RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding  = DataBindingUtil.inflate(LayoutInflater.from(context),
           R.layout.item_images,parent,false) as ItemImagesBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = imagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.path =imagesList[position]
        holder.binding.ivImage.layoutParams.height = width
        holder.binding.ivImage.layoutParams.width = width
        holder.binding.ivImage.invalidate()

    }

    inner class ViewHolder(var binding: ItemImagesBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}