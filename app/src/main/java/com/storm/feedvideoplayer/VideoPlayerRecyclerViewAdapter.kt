package com.storm.feedvideoplayer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.storm.feedvideoplayer.data.VideoDetailModel

class VideoPlayerRecyclerViewAdapter(
    private val viewModelList:List<VideoDetailModel>
) : RecyclerView.Adapter<VideoPlayerRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_recycler_view_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            title.text = viewModelList[position].title
            publisherName.text = viewModelList[position].subtitle
            videoDescription.text = viewModelList[position].description
            videoUrl.text = viewModelList[position].sources
        }
    }

    override fun getItemCount(): Int {
        return viewModelList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title:TextView = view.findViewById(R.id.video_title)
        var publisherName: TextView = view.findViewById(R.id.publisher_name)
        var videoDescription:TextView = view.findViewById(R.id.video_description)
        var videoUrl :  TextView = view.findViewById(R.id.tvVideoUrl)
        var clickArea : ConstraintLayout = view.findViewById(R.id.clickArea)
        var expandableTextImg :ImageView = view.findViewById(R.id.expandable_text_img)

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
    }

}