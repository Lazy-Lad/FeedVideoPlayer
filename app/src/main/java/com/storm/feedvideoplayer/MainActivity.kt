package com.storm.feedvideoplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.storm.feedvideoplayer.databinding.ActivityMainBinding
import com.storm.feedvideoplayer.util.jsonFileToList

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var view: View
    private lateinit var adapter:VideoPlayerRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        view = binding.root
        setContentView(view)
        val videoList = jsonFileToList("videoDetails.json")
        Log.d("JSON_FILE", videoList?.get(0)!!.toString())
        if(videoList.isNotEmpty()){
            adapter = VideoPlayerRecyclerViewAdapter(videoList)
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = adapter
        }else{
            Toast.makeText(this, "We don't have data right now.", Toast.LENGTH_SHORT).show()
        }
    }
}