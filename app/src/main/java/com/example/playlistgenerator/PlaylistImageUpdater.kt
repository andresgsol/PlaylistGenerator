package com.example.playlistgenerator

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistImageUpdater(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {
    private val retrofitService by lazy {
        RetrofitService.create("https://api.spotify.com/v1/")
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val playlist_id = inputData.getString("id")!!
            val access_token = "Bearer " + inputData.getString("access_token")
            val content_type = "image/jpeg"
            val image = inputData.getString("image")!!
            val call = retrofitService.changeCover(playlist_id, access_token, content_type, image)
            val result = call.execute()
            if (result.isSuccessful) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (error: Throwable) {
            Log.d("Error", error.toString())
            Result.failure()
        }
    }
}