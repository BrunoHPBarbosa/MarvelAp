package com.example.marvelapi.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marvelapi.data.model.character.CharacterModelResponse
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
class SearchCharacterViewModel @Inject constructor(
    private val repository:MarvelRepository
):ViewModel() {
    private val _searchCharacter = MutableStateFlow<ResourceState<CharacterModelResponse>>(ResourceState.Empty())
    val searchCharacter : StateFlow<ResourceState<CharacterModelResponse>> = _searchCharacter

    fun fetch(nameStartsWith: String) = viewModelScope.launch {
        safeFetch(nameStartsWith)
    }



    private suspend fun safeFetch(nameStartsWith: String) {
_searchCharacter.value = ResourceState.Loading()
        try {
            val response = repository.list(nameStartsWith)
            _searchCharacter.value = handleResponse(response)
        }catch(t: Throwable){
            when(t){
                is IOException -> _searchCharacter.value = ResourceState.Error("Erro na rede")
                else -> _searchCharacter.value = ResourceState.Error("Erro na conversao")
            }
        }

    }
    private fun handleResponse(response: Response<CharacterModelResponse>): ResourceState<CharacterModelResponse> {
if(response.isSuccessful){
    response.body()?.let { values ->
        return ResourceState.Success(values)
    }
}
        return ResourceState.Error(response.message())
    }
}
