package com.example.lio.postsocial.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.lio.postsocial.R
import com.example.lio.postsocial.other.EventObserver
import com.example.lio.postsocial.ui.main.viewmodels.CreatePostViewModel
import com.example.lio.postsocial.ui.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_post.*

@AndroidEntryPoint
class CreatePostFragment: Fragment(R.layout.fragment_create_post) {

    private val viewModel: CreatePostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                createPostProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { createPostProgressBar.isVisible = true }
        ) {
            createPostProgressBar.isVisible = false
            findNavController().popBackStack()
        })
    }
}