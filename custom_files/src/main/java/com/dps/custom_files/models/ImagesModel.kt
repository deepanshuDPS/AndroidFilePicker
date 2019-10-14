package com.dps.custom_files.models

import com.dps.custom_files.app_helper.AppConstants.FileType

data class ImagesModel(var imageSize:Int, var imagePath:String,var isChecked:Boolean,var fileType:FileType)