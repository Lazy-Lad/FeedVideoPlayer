package com.storm.feedvideoplayer

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.storm.feedvideoplayer.customRecyclerView.VideoPlayerRecyclerView
import com.storm.feedvideoplayer.customRecyclerView.VideoPlayerRecyclerViewAdapter
import com.storm.feedvideoplayer.databinding.ActivityMainBinding
import com.storm.feedvideoplayer.util.jsonFileToList

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var view: View
    private lateinit var adapter: VideoPlayerRecyclerViewAdapter
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        val videoList = jsonFileToList("videoDetails.json")
        Log.d("JSON_FILE", videoList?.get(0)!!.toString())
        if (videoList.isNotEmpty()) {

            adapter = VideoPlayerRecyclerViewAdapter(videoList,initGlide())
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.videoList = videoList
            binding.recyclerView.adapter = adapter
        } else {
            Toast.makeText(this, "We don't have data right now.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initGlide(): RequestManager {
        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.exo_controls_play)
            .error(R.drawable.ic_baseline_error_outline_24)
        return Glide.with(this)
            .setDefaultRequestOptions(options)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.recyclerView.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        binding.recyclerView.pausePlayer()
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerView.resumePlayer()
    }
}
