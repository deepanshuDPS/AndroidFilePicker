package com.dps.custom_file_picker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dps.custom_file_picker.R
import com.dps.custom_file_picker.app_helper.Utilities
import com.dps.custom_file_picker.databinding.ItemDatesBinding
import com.dps.custom_file_picker.listeners.IsAnyCheckedListener
import com.dps.custom_file_picker.listeners.OnFileSelectedListener
import com.dps.custom_file_picker.listeners.OnMultipleFilesSelectionListener
import com.dps.custom_file_picker.models.ImagesModel

class DatesAdapter(
    private var context: Context,
    private var dateList: ArrayList<String>,
    private var allImagesList: LinkedHashMap<String, ArrayList<ImagesModel>>,
    private var width: Int,
    private var onMultipleFilesSelectionListener: OnMultipleFilesSelectionListener,
    private var onFileSelectedListener: OnFileSelectedListener,
    private var isMultipleAllowed:Boolean
) : RecyclerView.Adapter<DatesAdapter.ViewHolder>() {

    private var isCheckedMain = false
    private var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.item_dates, parent, false
        ) as ItemDatesBinding
        return ViewHolder(binding)
    }

    override fun getItemCount() = dateList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var dateToSet = dateList[position]
        val currentYear = Utilities.getCurrentYear()
        if (dateToSet.contains(currentYear))
            dateToSet = dateToSet.replace(", $currentYear", "")
        // set up recycler view
        val imagesList = allImagesList[dateList[position]]!!
        val isAnyCheckedListener = object : IsAnyCheckedListener {
            override fun isAnyChecked(checked: Boolean) {
                if (checked) {
                    isCheckedMain = checked
                    count+=1
                }
                else {
                    count-=1
                    var flag = false
                    val hmIterator = allImagesList.entries.iterator()
                    while (hmIterator.hasNext()) {
                        val entry = hmIterator.next()
                        val images = entry.value
                        for (i in 0 until images.size) {
                            val image = images[i]
                            if (image.isChecked) {
                                flag = true
                                break
                            }
                        }
                        if (flag) break
                    }
                    isCheckedMain = flag
                }
                onMultipleFilesSelectionListener.onMultipleFileSelected(isCheckedMain,count)
                notifyDataSetChanged()
            }
        }

        val imagesAdapter = ImagesAdapter(context, imagesList, width, isCheckedMain, isAnyCheckedListener,onFileSelectedListener,isMultipleAllowed)
        holder.binding.apply {
            date = dateToSet
            imagesAdapter.setHasStableIds(true)
            rvImages.adapter = imagesAdapter
            rvImages.layoutManager = GridLayoutManager(context, 3)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun clearAllChecked(){
        isCheckedMain = false
        count = 0
        val hmIterator = allImagesList.entries.iterator()
        while (hmIterator.hasNext()) {
            val entry = hmIterator.next()
            val images = entry.value
            for (i in 0 until images.size)
                images[i].isChecked = false
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding: ItemDatesBinding) : RecyclerView.ViewHolder(binding.root)
}