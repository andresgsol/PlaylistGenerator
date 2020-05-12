package com.example.playlistgenerator

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyViewModel(application: Application) : AndroidViewModel(application) {

    var access_token = "" // we get this from the api when user logs in


    private val repository: AppRepository = AppRepository(
        AppDatabase.getDatabase(application).playlistDao()
    )



    fun getPlaylists(): LiveData<List<PlaylistEntity>> {
        return repository.playlists
    }


    fun insertPlaylist(name: String?, uri: String?, external_url: String?, image_uri: String?) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        Log.d("insert", "$name, $uri, $external_url, $image_uri")
        if (name!=null && uri!=null && external_url!=null && image_uri!= null) {
            repository.insertPlaylist(PlaylistEntity(null, name, uri, external_url, image_uri))
        }
    }


    fun updateImage(tid: Long, new_image_uri: String?) = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
        Log.d("update image", "$tid, $new_image_uri")
        repository.updateImage(tid, new_image_uri)
    }



    fun setAccessToken(token: String) {
        access_token = token
        Log.d("access_token", access_token)
    }




}