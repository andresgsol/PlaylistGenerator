package com.example.playlistgenerator

import kotlin.collections.HashMap

data class RecommendationItem (
    var tracks: Array<TrackItemSimplified>,
    var seeds: Array<SeedItem>
)

data class TrackItemSimplified (
    var artists: Array<ArtistItem>,
    var available_markets: Array<String>,
    var disc_number: Int,
    var duration_ms: Int,
    var explicit: Boolean,
    var external_urls: HashMap<String, String>,
    var href: String,
    var id: String,
    var is_playable: Boolean,
    var linked_from: LinkedTrackItem,
    var restrictions: HashMap<String, String>,
    var name: String,
    var preview_url: String,
    var track_number: Int,
    var type: String,
    var uri: String,
    var is_local: Boolean
)

data class ArtistItem (
    var external_urls: HashMap<String, String>,
    var href: String,
    var id: String,
    var name: String,
    var type: String,
    var uri: String
)

data class LinkedTrackItem (
    var external_urls: HashMap<String, String>,
    var href: String,
    var id: String,
    var type: String,
    var uri: String
)

data class SeedItem (
    var afterFilteringSize: Int,
    var afterRelinkingSize: Int,
    var href: String,
    var id: String,
    var initialPoolSize: Int,
    var type: String
)

data class TrackItemVerySimplified (
    var artists: String,
    var name: String,
    var uri: String
)

data class User (
    var id: String
)

data class PlaylistName (
    var name: String
)
data class Playlist (
    var id: String,
    val uri: String,
    val external_urls: HashMap<String, String>
)

data class Tracks (
    var uris: Array<String>
)

data class Image (
    var heigth: Int,
    var width: Int,
    var url: String
)

data class CreatePlaylistBody (
    var name: String,
    var public: Boolean
)