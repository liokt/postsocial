package com.example.lio.postsocial.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.lio.postsocial.R
import com.example.lio.postsocial.other.EventObserver
import com.example.lio.postsocial.ui.main.viewmodels.CreatePostViewModel
import com.example.lio.postsocial.ui.slideUpViews
import com.example.lio.postsocial.ui.snackbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_post.*
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment: Fragment(R.layout.fragment_create_post) {

    @Inject
    lateinit var glide: RequestManager

    private val viewModel: CreatePostViewModel by viewModels()

    private var curImgUri: Uri? = null
    private lateinit var cropContent: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.setCurImageUri(it)
            }
        }
    }

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>(){
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(16,9)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())

        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent).uri
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        btnSetPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        ivPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        btnPost.setOnClickListener {
            curImgUri?.let { uri ->
                viewModel.createPost(uri, etPostDescription.text.toString())
            } ?: snackbar(getString(R.string.error_no_image_chosen))
        }

        slideUpViews(requireContext(), ivPostImage, btnSetPostImage, tilPostText, btnPost)
    }

    private fun subscribeToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImgUri = it
            btnSetPostImage.isVisible = false
            glide.load(curImgUri).into(ivPostImage)
        }
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