package com.example.playlistgenerator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationResponse


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("MainActivity", "onActivityResult")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1337) {
            val response: AuthenticationResponse = AuthenticationClient.getResponse(resultCode,data)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> {
                    this.let { ViewModelProviders.of(it).get(MyViewModel::class.java)}.setAccessToken(response.accessToken)
                    (supportFragmentManager.fragments[0].childFragmentManager.fragments[0] as LoginFragment).navigate()
                }
                AuthenticationResponse.Type.ERROR -> {
                    Log.e("Error","Response: ${response.error}")
                }
                else -> {
                }
            }
        }

    }


}