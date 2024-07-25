package com.example.marvelapi.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.marvelapi.R
import com.example.marvelapi.data.model.character.CharacterModel
import com.example.marvelapi.databinding.FragmentDetailsCharacterBinding
import com.example.marvelapi.ui.adapters.ComicAdapter
import com.example.marvelapi.ui.base.BaseFragment
import com.example.marvelapi.ui.state.ResourceState
import com.example.marvelapi.util.hide
import com.example.marvelapi.util.limitDescription
import com.example.marvelapi.util.loadImage
import com.example.marvelapi.util.show
import com.example.marvelapi.util.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DetailCharacterFragment :
    BaseFragment<FragmentDetailsCharacterBinding, DetailCharacterViewModel>() {
    override val viewModel: DetailCharacterViewModel by viewModels()

    private val args: DetailCharacterFragmentArgs by navArgs()

    private val comicAdapter by lazy {
        ComicAdapter()
    }
    private lateinit var characterModel: CharacterModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.character
        viewModel.fetch(characterModel.id)
        setupRecyclerView()
        onLoadCharacter(characterModel)
        collectObserver()
        descriptionCharacter()
    }

    private fun descriptionCharacter() {
        binding.tvDescriptionCharacterDetails.setOnClickListener {
            onShowDielog(characterModel)
        }
    }

    private fun onShowDielog(characterModel: CharacterModel) {
MaterialAlertDialogBuilder(requireContext())
    .setTitle(characterModel.description)
    .setMessage(characterModel.description)
    .setNegativeButton(getString(R.string.close_dialog)){dielog,_ ->
        dielog.dismiss()
    }
    .show()
    }

    private fun collectObserver()  = lifecycleScope.launch{
       viewModel.detail.collect{ result ->
           when(result){
               is ResourceState.Success -> {
binding.progressBarDetail.hide()
                   result.data?.let { values ->
                        if(values.data.result.count() > 0){
                        comicAdapter.comics = values.data.result.toList()
                        }else{
                       toast(getString(R.string.empty_list_comics))
                        }
                   }
               }
               is ResourceState.Error -> {
binding.progressBarDetail.hide()
                   result.message?.let { message ->
                       Timber.tag("DetailCharacter").e("error -> $message")
                      toast(message)
                   }
               }
               is ResourceState.Loading -> {
binding.progressBarDetail.show()
               }
               else ->{}
           }
       }

    }

    private fun onLoadCharacter(characterModel: CharacterModel) = with(binding) {
        tvNameCharacterDetails.text = characterModel.name
        if(characterModel.description.isEmpty()){
     tvNameCharacterDetails.text = requireContext().getString(R.string.text_description_empty)
        }else{
 tvNameCharacterDetails.text = characterModel.description.limitDescription(100)
        }
        loadImage(
            imgCharacterDetails,
            characterModel.thumbnailModel.path,
            characterModel.thumbnailModel.extension
            )

    }

    private fun setupRecyclerView() = with(binding) {
        rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite ->{
              viewModel.insert(characterModel)
                toast(getString(R.string.saved_successfully))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}