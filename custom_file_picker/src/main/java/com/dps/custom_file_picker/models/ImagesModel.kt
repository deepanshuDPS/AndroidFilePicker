package com.dps.custom_file_picker.models

import com.dps.custom_file_picker.app_helper.AppConstants.FileType

data class ImagesModel(var imageSize:Int, var imagePath:String,var isChecked:Boolean,var fileType:FileType)