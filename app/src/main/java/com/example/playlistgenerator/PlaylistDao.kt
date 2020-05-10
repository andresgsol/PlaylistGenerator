package com.example.playlistgenerator

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists")
    fun getAll(): LiveData<List<PlaylistEntity>>
    @Insert
    fun insert(playlist: PlaylistEntity): Long
    @Query("UPDATE playlists SET image_uri = :new_image_uri WHERE playlistId = :tid")
    fun updateImage(tid: Long, new_image_uri: String?): Int
    @Query("DELETE FROM playlists")
    fun deleteAll()
}

