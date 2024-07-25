package com.example.marvelapi.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marvelapi.data.model.character.CharacterModel
import com.example.marvelapi.repository.MarvelRepository
import com.example.marvelapi.ui.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
): ViewModel() {

    private val _favorites = MutableStateFlow<ResourceState<List<CharacterModel>>>(ResourceState.Empty())
    val favorites: StateFlow<ResourceState<List<CharacterModel>>> = _favorites

    init {

        fech()
    }

    private fun fech() = viewModelScope.launch {
        repository.getAll().collectLatest { characters ->
            if (characters.isEmpty()) {
                _favorites.value = ResourceState.Empty()
            } else {
                _favorites.value = ResourceState.Success(characters)
            }
        }
    }
        fun delete(characterModel: CharacterModel) = viewModelScope.launch {
            repository.delete(characterModel)
        }

    }

