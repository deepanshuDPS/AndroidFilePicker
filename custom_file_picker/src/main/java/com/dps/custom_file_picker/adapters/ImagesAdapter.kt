package com.dps.custom_file_picker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_file_picker.R
import com.dps.custom_file_picker.app_helper.AppConstants.FileType
import com.dps.custom_file_picker.databinding.ItemImagesBinding
import com.dps.custom_file_picker.listeners.IsAnyCheckedListener
import com.dps.custom_file_picker.listeners.OnFileSelectedListener
import com.dps.custom_file_picker.models.ImagesModel

class ImagesAdapter(
    private var context: Context,
    private var imagesList: ArrayList<ImagesModel>,
    private var width: Int,
    private var isChecked: Boolean,
    private var isAnyCheckedListener: IsAnyCheckedListener,
    private var onFileSelectedListener: OnFileSelectedListener,
    private var isMultipleAllowed:Boolean
) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_images, parent, false
        ) as ItemImagesBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = imagesList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imagesModel = imagesList[position]
        holder.binding.image = imagesModel
        holder.binding.ivImage.layoutParams.height = width
        holder.binding.ivImage.layoutParams.width = width
        holder.binding.ivImage.invalidate()
        if(imagesModel.fileType == FileType.VIDEO)
            holder.binding.ivVideo.visibility =View.VISIBLE
        else
            holder.binding.ivVideo.visibility =View.GONE

        if (isChecked) {
            holder.binding.ibCheck.visibility = View.VISIBLE
            if (imagesModel.isChecked)
                holder.binding.ibCheck.setImageResource(R.drawable.ic_checked)
            else {
                holder.binding.ibCheck.setImageDrawable(null)
                holder.binding.ibCheck.setBackgroundResource(R.drawable.bg_white_circle)
            }
        } else {
            holder.binding.ibCheck.visibility = View.GONE
        }

        holder.bindClickableViews(imagesModel)
    }

    inner class ViewHolder(var binding: ItemImagesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindClickableViews(imagesModel: ImagesModel) {

            fun makeCheckable() {
                imagesModel.isChecked = !imagesModel.isChecked
                isAnyCheckedListener.isAnyChecked(imagesModel.isChecked)
            }

            binding.root.setOnLongClickListener {
                if(isMultipleAllowed)
                    makeCheckable()
                return@setOnLongClickListener true
            }

            binding.root.setOnClickListener {

                if (isChecked)
                    makeCheckable()
                else {
                    onFileSelectedListener.onFileSelected(imagesModel.imagePath)
                }

            }

            binding.ibCheck.setOnClickListener {
                makeCheckable()
            }
        }
    }
}