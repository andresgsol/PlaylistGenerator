package com.example.playlistgenerator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse


class LoginFragment : Fragment() {

    private val REQUEST_CODE = 1337
    private val redirectUri = "com.example.playlistgenerator://callback"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_login, container, false)
        (v.findViewById(R.id.button) as Button).setOnClickListener {
            login(activity as MainActivity)
        }
        return v
    }

    fun navigate() {
        view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)
    }

    fun login(activity: MainActivity): Int {
        val builder = AuthenticationRequest.Builder(resources.getString(R.string.client_ID), AuthenticationResponse.Type.TOKEN,redirectUri)
        builder.setScopes(arrayOf("streaming","user-read-private","playlist-modify-public","user-read-email","ugc-image-upload","playlist-modify-private"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(activity,REQUEST_CODE,request)
        return 1
    }
}
