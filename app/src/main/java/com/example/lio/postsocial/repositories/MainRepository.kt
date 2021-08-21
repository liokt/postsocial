package com.example.lio.postsocial.repositories

import android.net.Uri
import com.example.lio.postsocial.data.entities.User
import com.example.lio.postsocial.other.Resource

interface MainRepository {

    suspend fun createPost(imageUri: Uri, text: String): Resource<Any>

    suspend fun getUsers(uids: List<String>): Resource<List<User>>

    suspend fun getUser(uid: String) : Resource<User>
}