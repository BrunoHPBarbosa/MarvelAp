package com.example.marvelapi.data.local

import androidx.room.TypeConverter
import com.example.marvelapi.data.model.ThumbnailModel
import com.google.gson.Gson

class MarvelConverter {

    @TypeConverter
    fun fromThumbnail(thumbnailModel: ThumbnailModel): String = Gson().toJson(thumbnailModel)
    @TypeConverter
    fun toThumbnail(thumbnailModel: String): ThumbnailModel = Gson().fromJson(thumbnailModel ,ThumbnailModel::class.java)
}