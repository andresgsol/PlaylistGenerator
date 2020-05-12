package com.example.playlistgenerator

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    private var model: MyViewModel? = null

    var targetPlaylist: Long? = null
    var targetExternalPlaylist: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_home, container, false)


        val recyclerView = v.findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = PlaylistAdapter()
        recyclerView.adapter = adapter


        model = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java)}


        model?.getPlaylists()?.observe(viewLifecycleOwner, Observer{ playlists ->
            playlists?.let{adapter.setPlaylists(it)}
        })


        v.findViewById<FloatingActionButton>(R.id.add).setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_surveyFragment)
        }
        return v
    }



    inner class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
        private var playlists = emptyList<PlaylistEntity>()

        internal fun setPlaylists(playlists: List<PlaylistEntity>) {
            this.playlists = playlists.reversed()
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return playlists.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_view_holder, parent, false)
            return PlaylistViewHolder(v)
        }

        override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
            holder.bindItems(playlistEntity = this.playlists[position])
        }


        inner class PlaylistViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bindItems(playlistEntity: PlaylistEntity) {
                if (playlistEntity.image_uri != null) {
                    Glide.with(this@HomeFragment)
                        .load(Uri.parse(playlistEntity.image_uri))
                        .apply(RequestOptions().override(200, 200))
                        .into(itemView.findViewById(R.id.pic))
                }
                else {//default image
                    Glide.with(this@HomeFragment)
                        .load(R.drawable.ic_launcher_background)
                        .apply(RequestOptions().override(200, 200))
                        .into(itemView.findViewById(R.id.pic))
                }
                itemView.findViewById<TextView>(R.id.name).text = playlistEntity.title
                itemView.findViewById<ImageButton>(R.id.spotify_button).setOnClickListener {
                    // open playlist in browser
                    val uris = Uri.parse(playlistEntity.external_url)
                    val intents = Intent(Intent.ACTION_VIEW, uris)
                    val b = Bundle()
                    b.putBoolean("new_window", true)
                    intents.putExtras(b)
                    context?.startActivity(intents)
                }

            }

        }
    }

}
