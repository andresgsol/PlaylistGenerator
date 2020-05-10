package com.example.playlistgenerator

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface  RetrofitService {


    @GET("recommendations")
    fun getRecommendationsAsync(@Header("Authorization") auth: String,
                                @Query("limit") limit: Int,
                                @Query("seed_genres") genres: String,
                                @Query("target_energy") energy: Double,
                                @Query("target_loudness") loud: Double,
                                @Query("target_danceability") dance: Double,
                                @Query("min_tempo") tempo: Int,
                                @Query("target_valence") valence: Double,
                                @Query("target_mode") mode: String
    ): Deferred<RecommendationItem>
    @GET("recommendations")
    fun getRecommendationsWithoutModeAsync(@Header("Authorization") auth: String,
                                           @Query("limit") limit: Int,
                                           @Query("seed_genres") genres: String,
                                           @Query("target_energy") energy: Double,
                                           @Query("target_loudness") loud: Double,
                                           @Query("target_danceability") dance: Double,
                                           @Query("min_tempo") tempo: Int,
                                           @Query("target_valence") valence: Double
    ): Deferred<RecommendationItem>

    @GET("me")
    fun getUser(@Header("Authorization") auth: String): Call<User>
    @POST("users/{id}/playlists")
    fun createPlaylist(@Path("id") id: String, @Header("Authorization") auth: String,
                     @Header("Content-Type") type: String, @Body playlist: PlaylistName): Call<Playlist>
    @POST("playlists/{id}/tracks")
    fun addTrack(@Path("id") id: String, @Header("Authorization") auth: String,
                 @Header("Content-Type") type: String, @Body tracks: Tracks): Call<JSONObject>

    @PUT("playlists/{id}/images")
    fun changeCover(@Path("id") id: String, @Header("Authorization") auth: String,
                    @Header("Content-Type") type: String, @Body img: String): Call<JSONObject>

    companion object {
        fun create(baseUrl: String): RetrofitService {

            val retrofit = Retrofit.Builder().addCallAdapterFactory(CoroutineCallAdapterFactory()).addConverterFactory(
                    GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()))
                .baseUrl(baseUrl)
                .build()

            return retrofit.create(RetrofitService::class.java)
        }
    }
}
