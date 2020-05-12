package com.example.playlistgenerator

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf


// audio features:
// https://developer.spotify.com/documentation/web-api/reference/tracks/get-audio-features/


class SurveyFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_survey, container, false)

        val model = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java)}

        // Check if num_songs is empty and enable or disble the submit button accordingly
        v.findViewById<EditText>(R.id.num_songs).doAfterTextChanged {
            v.findViewById<Button>(R.id.submit).isEnabled = it?.length!! > 0
        }

        // Add genre
        val genresLay = v.findViewById<LinearLayout>(R.id.genres_layout)
        v.findViewById<Button>(R.id.genres_button).setOnClickListener {
            addEditText(genresLay)
        }
        addEditText(genresLay)  // initial genre

        // Add keyword
        val keywordsLay = v.findViewById<LinearLayout>(R.id.keywords_layout)
        v.findViewById<Button>(R.id.keywords_button).setOnClickListener {
            addEditText(keywordsLay)
        }
        addEditText(keywordsLay)  // initial genre


        // Submit survey on "generate" button click
        v.findViewById<Button>(R.id.submit).setOnClickListener {
            val num = v.findViewById<TextView>(R.id.num_songs).text.toString().toInt()

            val genres = mutableListOf<String>()
            for (i in 0 until genresLay.childCount) {
                genres.add((genresLay[i] as EditText).text.toString().toLowerCase())
            }

            val keywords = mutableListOf<String>()
            for (i in 0 until keywordsLay.childCount) {
                keywords.add((keywordsLay[i] as EditText).text.toString())
            }

            val mood = mutableListOf<String>()
            val moodLay = v.findViewById<LinearLayout>(R.id.mood_layout)
            for (i in 0 until moodLay.childCount) {
                mood.add(((moodLay[i] as SeekBar).progress/100.0).toString())
            }


            //enqueue worker
            val worker = OneTimeWorkRequestBuilder<PlaylistWorker>()
                .setInputData(workDataOf("access_token" to model?.access_token, "num" to num,
                    "genres" to genres.toTypedArray(), "keywords" to keywords.toTypedArray(),
                    "mood" to mood.toTypedArray()))
                .build()
            WorkManager.getInstance().enqueue(worker)


            v?.findNavController()?.navigate(
                R.id.action_surveyFragment_to_resultsFragment, bundleOf("workerID" to worker.id,
                    "num" to num,
                    "genres" to genres.toTypedArray(), "keywords" to keywords.toTypedArray(),
                    "mood" to mood.toTypedArray())
            )
        }

        return v
    }


    // Auxiliary function that adds a EditText to a LinearLayout
    private fun addEditText(layout: LinearLayout) {
        // Only add if it's the first one or if the last element added is not empty
        if (layout.childCount == 0 || (layout[layout.childCount-1] as TextView).text.toString() != "") {
            val editText = EditText(this.context)
            // If loses focus and it's empty, remove it
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus && editText.text.isEmpty() && layout.childCount > 1) {
                    layout.removeView(editText)
                }
            }
            layout.addView(editText)
        }
    }


}
