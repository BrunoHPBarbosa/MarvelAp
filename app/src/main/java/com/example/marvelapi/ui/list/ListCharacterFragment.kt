package com.example.marvelapi.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvelapi.R
import com.example.marvelapi.databinding.FragmentListCharacterBinding
import com.example.marvelapi.ui.adapters.CharacterAdapter
import com.example.marvelapi.ui.base.BaseFragment
import com.example.marvelapi.ui.state.ResourceState
import com.example.marvelapi.util.hide
import com.example.marvelapi.util.show
import com.example.marvelapi.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.locks.ReentrantLock

@AndroidEntryPoint
class ListCharacterFragment: BaseFragment<FragmentListCharacterBinding,ListCharacterViewModel>() {


    override val viewModel: ListCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickAdapter()
        colectObserver()
    }

    private fun colectObserver() = lifecycleScope.launch {
        viewModel.list
            .collect{ resource ->
                when(resource){
                    is ResourceState.Success ->{
                        resource.data?.let{ values ->
binding.progressCircular.hide()
                            characterAdapter.characters = values.data.results.toList()
                        }

                    }
                    is ResourceState.Error ->{
                        binding.progressCircular.hide()
                        resource.message?.let{ message ->
                            toast(getString(R.string.an_error_occurred))
                            Timber.tag("ListCharacterFragment").e("Error -> $message")
                        }

                    }
                    is ResourceState.Loading ->{
                        binding.progressCircular.show()

                    }
                    else ->{}
                }

            }
    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener { characterModel ->
            val action = ListCharacterFragmentDirections
                .actionListCharacterFragmentToDetailCharacterFragment(characterModel)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding){
        rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())

        }
    }


    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCharacterBinding =
        FragmentListCharacterBinding.inflate(inflater,container,false)
}