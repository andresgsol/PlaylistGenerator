package com.example.playlistgenerator

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistPoster(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    private val retrofitService by lazy {
        RetrofitService.create("https://api.spotify.com/v1/")
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        return@withContext try {
            val access_token = "Bearer " + inputData.getString("access_token")
            val content_type = "application/json"
            val call = retrofitService.getUser(access_token)
            val result = call.execute()
            var user_id = ""
            if (result.isSuccessful) {
                user_id = (result.body() as User).id
            }
            if (user_id == "") {
                Result.failure()
            }
            val call2 = retrofitService.createPlaylist(user_id, access_token, content_type, CreatePlaylistBody(inputData.getString("name")!!, inputData.getBoolean("public", false)))
            val result2 = call2.execute()
            var playlist_id = ""
            var uri = ""
            var external_urls: HashMap<String, String>? = null
            if (result2.isSuccessful) {
                playlist_id = (result2.body() as Playlist).id
                uri = (result2.body() as Playlist).uri
                external_urls = (result2.body() as Playlist).external_urls
            }
            if (playlist_id == "") {
                Result.failure()
            }


            val body2 = Tracks(inputData.getStringArray("tracks")!!)
            val call3 = retrofitService.addTrack(playlist_id, access_token, content_type, body2)
            val result3 = call3.execute()
            if (result3.isSuccessful) {
                //Result.success(workDataOf("name" to inputData.getString("name"), "uri" to uri, "external_url" to (external_urls?.get("spotify"))))
            } else {
                Result.failure()
            }

            val request = retrofitService.getCover(playlist_id, access_token)
            val res = request.await()
            Result.success(workDataOf("name" to inputData.getString("name"), "uri" to uri,
                "external_url" to (external_urls?.get("spotify")), "cover" to res[0].url))


        } catch (error: Throwable) {
            Result.failure()
        }
    }
}