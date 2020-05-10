package com.example.playlistgenerator

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class AppRepository(private val playlistDao: PlaylistDao) {

    // List of all playlists (pulled from the db)
    var playlists: LiveData<List<PlaylistEntity>> = playlistDao.getAll()

    @WorkerThread
    fun insertPlaylist(playlistEntity: PlaylistEntity): Long {
        return playlistDao.insert(playlistEntity)
    }

    @WorkerThread
    fun deleteAllPlaylists() {
        playlistDao.deleteAll()
    }

    @WorkerThread
    fun updateImage(tid: Long, new_image_uri: String?): Int {
        return playlistDao.updateImage(tid, new_image_uri)
    }



}