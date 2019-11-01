package com.dps.custom_file_picker.models

import com.dps.custom_file_picker.app_helper.AppConstants.FileType

data class AlbumModel(var albumId:String, var albumName:String, var lastFilePath:String, var fileCount:Int, var imageSize:Int,var fileType:FileType)