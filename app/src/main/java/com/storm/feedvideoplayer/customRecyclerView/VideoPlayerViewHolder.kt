package com.storm.feedvideoplayer.customRecyclerView

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.storm.feedvideoplayer.R
import com.storm.feedvideoplayer.data.VideoDetailModel

class VideoPlayerViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {

    var parent:View = itemView
    var title: TextView = itemView.findViewById(R.id.video_title)
    var publisherName: TextView = itemView.findViewById(R.id.publisher_name)
    var videoDescription: TextView = itemView.findViewById(R.id.video_description)
    var clickArea : ConstraintLayout = itemView.findViewById(R.id.clickArea)
    var expandableTextImg : ImageView = itemView.findViewById(R.id.expandable_text_img)
    var thumbnail:ImageView = itemView.findViewById(R.id.thumbnail)
    var progressBar:ProgressBar = itemView.findViewById(R.id.exo_buffering_progress_bar)
    var dp:ImageView = itemView.findViewById(R.id.publisher_img)
    lateinit var requestManager:RequestManager

    init {
        clickArea.setOnClickListener{
            Log.d("ClickEvent","expandableBtn pressed")
            if(videoDescription.visibility == View.GONE){
                videoDescription.visibility = View.VISIBLE
                expandableTextImg.rotationX = 180f
            }else if(videoDescription.visibility == View.VISIBLE){
                videoDescription.visibility = View.GONE
                expandableTextImg.rotationX = 0f
            }
        }
    }

    fun onBind(videoDetailModel:VideoDetailModel,requestManager: RequestManager){
        this.requestManager = requestManager
        parent.tag = this
        title.text = videoDetailModel.title
        publisherName.text = videoDetailModel.subtitle
        videoDescription.text = videoDetailModel.description
        this.requestManager
            .load(videoDetailModel.thumb)
            .into(thumbnail)
        this.requestManager
            .load(videoDetailModel.profileImg)
            .into(dp)
    }
}