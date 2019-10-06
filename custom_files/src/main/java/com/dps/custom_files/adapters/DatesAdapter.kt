package com.dps.custom_files.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_files.R
import com.dps.custom_files.databinding.ItemDatesBinding

class DatesAdapter(private var context: Context, private var dateList:ArrayList<String>,private var imagesList:LinkedHashMap<String, ArrayList<String>>, private var width:Int): RecyclerView.Adapter<DatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding  = DataBindingUtil.inflate(LayoutInflater.from(context),
           R.layout.item_dates,parent,false) as ItemDatesBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = dateList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.date = dateList[position]
        // set up recycler view
        val imagesAdapter = ImagesAdapter(context,imagesList[dateList[position]]!!,width)
        holder.binding.rvImages.adapter = imagesAdapter
        holder.binding.rvImages.layoutManager = GridLayoutManager(context,4)

    }

    inner class ViewHolder(var binding: ItemDatesBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}