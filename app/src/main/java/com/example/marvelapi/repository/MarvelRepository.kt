package com.example.marvelapi.repository

import com.example.marvelapi.data.local.MarvelDao
import com.example.marvelapi.data.model.character.CharacterModel
import com.example.marvelapi.data.remote.ServiceApi
import javax.inject.Inject

class MarvelRepository @Inject constructor(
    private val api: ServiceApi,
    private val dao: MarvelDao
) {
    suspend fun list(nameStartsWith: String? = null) = api.list(nameStartsWith)
    suspend fun getComics(characterId: Int) = api.getComics(characterId)

    suspend fun insert(characterModel: CharacterModel) = dao.insert(characterModel)
    fun getAll() = dao.getAll()
    suspend fun delete(characterModel: CharacterModel) = dao.delete(characterModel)
}