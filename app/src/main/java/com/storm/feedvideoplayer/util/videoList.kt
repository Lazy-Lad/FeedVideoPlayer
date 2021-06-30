package com.storm.feedvideoplayer.util

import com.storm.feedvideoplayer.data.VideoDetailModel

class VideoListData(){

    private lateinit var list: List<VideoDetailModel>
    fun getVideoList():List<VideoDetailModel>{
        return list
    }
    fun setVideoList(list: List<VideoDetailModel>){
        this.list = list
    }
}