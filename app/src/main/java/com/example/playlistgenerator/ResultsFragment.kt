package com.example.playlistgenerator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.*


class ResultsFragment : Fragment() {

    private var model: MyViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_results, container, false)

        val num = arguments?.getInt("num")
        val genres = arguments?.getStringArray("genres")
        val keywords = arguments?.getStringArray("keywords")
        val mood = arguments?.getStringArray("mood")

        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = PlaylistAdapter()
        recyclerView.adapter = adapter


        model = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java)}


        observeWorker(arguments?.get("workerID") as UUID, adapter, v)



        v.findViewById<Button>(R.id.nay).setOnClickListener { //GENERATE NEW PLAYLIST

            //enqueue worker
            val worker = OneTimeWorkRequestBuilder<PlaylistWorker>()
                .setInputData(
                    workDataOf("access_token" to model?.access_token, "num" to num,
                        "genres" to genres,
                        "keywords" to keywords,
                        "mood" to mood)
                )
                .build()
            WorkManager.getInstance().enqueue(worker)


            //observe it
            observeWorker(worker.id, adapter, v)
        }

        v.findViewById<Button>(R.id.yay).setOnClickListener {
            var poster: OneTimeWorkRequest? = null
            if (v.findViewById<EditText>(R.id.editText).text.toString() != "") {
                val size = v.findViewById<RecyclerView>(R.id.recyclerView).adapter!!.itemCount
                val strArray = Array(size) {""}
                for (i in 1..size) {
                    strArray[i-1] = (v.findViewById<RecyclerView>(R.id.recyclerView).adapter!! as PlaylistAdapter).getItem(i-1).uri
                }
                poster = OneTimeWorkRequestBuilder<PlaylistPoster>()
                    .setInputData(
                        workDataOf("access_token" to model?.access_token,
                            "name" to v.findViewById<EditText>(R.id.editText).text.toString(),
                            "tracks" to strArray, "public" to v.findViewById<Switch>(R.id.switch1).isChecked)
                    ).build()
                WorkManager.getInstance().enqueue(poster)
            }
            else {
                Toast.makeText(this.context, "You must provide a name", Toast.LENGTH_SHORT).show()
            }

            poster?.id?.let { it1 -> observePoster(it1, v) }
        }



        return v
    }

    //auxiliary function
    private fun observeWorker(uuid: UUID, adapter: PlaylistAdapter, v: View) {
        v.findViewById<TextView>(R.id.loading).visibility=View.VISIBLE
        v.findViewById<ConstraintLayout>(R.id.lay).visibility=View.INVISIBLE
        WorkManager.getInstance().getWorkInfoByIdLiveData(uuid)
            .observe(viewLifecycleOwner, Observer { info ->
                if (info != null && info.state.isFinished) {
                    val tracks = info.outputData.getStringArray("tracks")
                    val newPlaylist = mutableListOf<TrackItemVerySimplified>()
                    var i = 0
                    while (i < tracks?.size!!) {
                        newPlaylist.add(TrackItemVerySimplified(name=tracks[i],artists=tracks[i+1],uri=tracks[i+2]))
                        i+=3
                    }
                    adapter.setPlaylist(newPlaylist.toList())
                    v.findViewById<TextView>(R.id.loading).visibility=View.INVISIBLE
                    v.findViewById<ConstraintLayout>(R.id.lay).visibility=View.VISIBLE
                }
            })
    }

    private fun observePoster(uuid: UUID, v: View) {
        v.findViewById<TextView>(R.id.loading).visibility=View.VISIBLE
        v.findViewById<ConstraintLayout>(R.id.lay).visibility=View.INVISIBLE
        WorkManager.getInstance().getWorkInfoByIdLiveData(uuid)
            .observe(viewLifecycleOwner, Observer { info ->
                if (info != null && info.state.isFinished) {
                    model?.insertPlaylist(info.outputData.getString("name"),
                        info.outputData.getString("uri"),info.outputData.getString("external_url"),
                        info.outputData.getString("cover"))

                    v.findViewById<TextView>(R.id.loading).visibility=View.INVISIBLE
                    v.findViewById<ConstraintLayout>(R.id.lay).visibility=View.VISIBLE
                    Toast.makeText(this.context, "Success!", Toast.LENGTH_SHORT).show()
                }
            })
    }


    inner class PlaylistAdapter : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
        private var playlist = emptyList<TrackItemVerySimplified>()

        internal fun setPlaylist(playlist: List<TrackItemVerySimplified>) {
            this.playlist = playlist
            notifyDataSetChanged()
        }

        fun getItem(pos: Int): TrackItemVerySimplified {
            return playlist[pos]
        }

        override fun getItemCount(): Int {
            return playlist.size
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.song_view_holder, parent, false)
            return PlaylistViewHolder(v)
        }

        override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
            holder.bindItems(trackItemVerySimplified = this.playlist[position])
        }


        inner class PlaylistViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun bindItems(trackItemVerySimplified: TrackItemVerySimplified) {
                itemView.findViewById<TextView>(R.id.name).text = trackItemVerySimplified.name
                itemView.findViewById<TextView>(R.id.artist).text = trackItemVerySimplified.artists
            }

        }
    }

}
