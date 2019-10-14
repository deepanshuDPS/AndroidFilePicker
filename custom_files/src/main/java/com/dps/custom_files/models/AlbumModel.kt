package com.dps.custom_files.models

import com.dps.custom_files.app_helper.AppConstants.FileType

data class AlbumModel(var albumId:String, var albumName:String, var lastFilePath:String, var fileCount:Int, var imageSize:Int,var fileType:FileType)