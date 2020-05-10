package com.example.playlistgenerator

import androidx.room.*

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) var playlistId: Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "uri") var uri: String,
    @ColumnInfo(name = "external_url") var external_url: String,
    @ColumnInfo(name = "image_uri") var image_uri: String?
)

