package com.dps.custom_files.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_files.R
import com.dps.custom_files.activities.AlbumsImagesActivity
import com.dps.custom_files.databinding.ItemGalleryAlbumBinding
import com.dps.custom_files.models.AlbumModel

class GalleryAdapter(private var context: Context,private var albumsList:ArrayList<AlbumModel>,private var width:Int): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding  = DataBindingUtil.inflate(LayoutInflater.from(context),
           R.layout.item_gallery_album,parent,false) as ItemGalleryAlbumBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = albumsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.album = albumsList[position]
        holder.binding.ivLastImage.layoutParams.width = width
        holder.binding.ivLastImage.layoutParams.height = width
        holder.binding.ivLastImage.invalidate()
        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context,AlbumsImagesActivity::class.java).putExtra("album_id",albumsList[position].albumId))
        }
    }

    inner class ViewHolder(var binding: ItemGalleryAlbumBinding) : RecyclerView.ViewHolder(binding.root) {

    }
}