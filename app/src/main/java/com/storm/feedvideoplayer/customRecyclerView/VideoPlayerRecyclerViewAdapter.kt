package com.storm.feedvideoplayer.customRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.storm.feedvideoplayer.R
import com.storm.feedvideoplayer.data.VideoDetailModel

class VideoPlayerRecyclerViewAdapter(
    private val viewModelList:List<VideoDetailModel>,
    private val requestManager: RequestManager
) : RecyclerView.Adapter<VideoPlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoPlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_recycler_view_item,parent,false)
        return VideoPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoPlayerViewHolder, position: Int) {
        val viewHolder = VideoPlayerViewHolder(holder.itemView)
        viewHolder.onBind(viewModelList[position],requestManager)
    }

    override fun getItemCount(): Int {
        return viewModelList.size
    }

}