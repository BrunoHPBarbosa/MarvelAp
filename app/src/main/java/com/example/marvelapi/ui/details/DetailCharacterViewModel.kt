package com.example.marvelapi.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marvelapi.data.model.character.CharacterModel
import com.example.marvelapi.data.model.comic.ComicModelResponse
import com.example.marvelapi.repository.MarvelRepository
import com.example.marvelapi.ui.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class DetailCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
) : ViewModel() {

    private val _detail =
        MutableStateFlow<ResourceState<ComicModelResponse>>(ResourceState.Loading())
    val detail: StateFlow<ResourceState<ComicModelResponse>> = _detail

    fun fetch(characterId: Int) = viewModelScope.launch {
        safeFecht(characterId)
    }


    private suspend fun safeFecht(characterId: Int) {
        _detail.value = ResourceState.Loading()
        try {
            val response = repository.getComics(characterId)
            _detail.value = handleResponse(response)
        } catch (t: Throwable) {
            when (t) {
                is IOException -> _detail.value = ResourceState.Error("Erro de rede")
                else -> _detail.value = ResourceState.Error("Erro na conversao")
            }



        }

    }

    private fun handleResponse(response: Response<ComicModelResponse>): ResourceState<ComicModelResponse> {
        if (response.isSuccessful) {
            response.body()?.let { values ->
                return ResourceState.Success(values)
            }

        }
        return ResourceState.Error(response.message())
    }

    fun insert(characterModel: CharacterModel) = viewModelScope.launch {
        repository.insert(characterModel)

    }
}