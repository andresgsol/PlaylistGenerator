package com.example.playlistgenerator

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.roundToInt


class PlaylistWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams) {

    private val retrofitService by lazy {
        RetrofitService.create("https://api.spotify.com/v1/")
    }


    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {

        return@withContext try {


            val access_token = "Bearer " + inputData.getString("access_token")
            val num = inputData.getInt("num", 0)
            val genres = inputData.getStringArray("genres")
            //val keywords = inputData.getStringArray("keywords")
            val mood = inputData.getStringArray("mood")!!.map{it.toDouble()}


            var seedGenres = genres?.joinToString(separator = ",")?.replace(" ", "-")
            val lines = mutableListOf<String>()
            if (seedGenres.isNullOrEmpty()) {
                val reader =
                    BufferedReader(InputStreamReader(applicationContext.assets.open("genres.txt")))
                var mLine = reader.readLine()
                while (mLine != null) {
                    lines.add(mLine)
                    mLine = reader.readLine()
                }
                seedGenres = lines.shuffled().take(5).joinToString(separator = ",")
            }
            Log.d("seedGenres", seedGenres)


            val target_energy = mood[0]
            val target_loudness = mood[1]
            val target_danceability = target_energy * 0.8 + target_loudness * 0.2
            val target_min_tempo = (target_energy * 60 + 60).roundToInt()
            val target_valence = 1 - mood[2] // mood[2] is moodiness
            val target_mode =
                if (target_valence > 0.8) "1" else (if (target_valence < 0.2) "0" else null)

            val request =
                if (target_mode == null) retrofitService.getRecommendationsWithoutModeAsync(
                    access_token,
                    num,
                    seedGenres,
                    target_energy,
                    target_loudness,
                    target_danceability,
                    target_min_tempo,
                    target_valence
                )
                else retrofitService.getRecommendationsAsync(
                    access_token,
                    num,
                    seedGenres,
                    target_energy,
                    target_loudness,
                    target_danceability,
                    target_min_tempo,
                    target_valence,
                    target_mode
                )



            val res = request.await()
            Log.d("result", res.toString())

            val tracks: Array<TrackItemSimplified> = res.tracks
            val tracksString = mutableListOf<String>()
            tracks.forEach { track ->
                tracksString.add(track.name); tracksString.add(track.artists[0].name); tracksString.add(track.uri)
            }


            Result.success(workDataOf("tracks" to tracksString.toTypedArray()))

        } catch (error: Throwable) {
            Result.failure()
        }



    }
}

