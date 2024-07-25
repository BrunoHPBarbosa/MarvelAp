package com.example.marvelapi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.marvelapi.data.model.character.CharacterModel

@Database(entities = [CharacterModel::class], version = 1, exportSchema = false)
@TypeConverters(MarvelConverter::class)
 abstract class MarvelDataBase: RoomDatabase() {
  abstract fun marvelDao(): MarvelDao
}