package com.storm.feedvideoplayer.customRecyclerView

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.storm.feedvideoplayer.R
import com.storm.feedvideoplayer.data.VideoDetailModel


class VideoPlayerRecyclerView(
    context: Context,
    @Nullable attrs: AttributeSet?
) : RecyclerView(context, attrs) {

    // functional vars
    private var ctx: Context = context.applicationContext
    var videoList: List<VideoDetailModel>? = null
    private var videoSurfaceDefaultHeight = 0
    private var screenDefaultHeight = 0
    private var playPosition = -1
    private var isVideoViewAdded = false
    private var playbackPosition: Long = 0
    private var isMuted: Boolean = false

    // UI vars
    private var thumbnail: ImageView? = null
    private lateinit var soundIndicator: ImageView
    private lateinit var retryBtn: ImageView
    private var progressBar: ProgressBar? = null
    private var viewHolderParent: View? = null
    private var frameLayout: FrameLayout? = null
    private var videoSurfaceView: PlayerView
    private var videoPlayer: SimpleExoPlayer?
    private lateinit var requestManager:RequestManager


    init {
        // get screen size for display
        val display = this.resources.displayMetrics
        val width = display.widthPixels
        val height = display.heightPixels
        //this is actually width of scree, assumption screen width is video view height
        videoSurfaceDefaultHeight = width
        screenDefaultHeight = height
        Log.d("Display", "$videoSurfaceDefaultHeight $screenDefaultHeight")

        // setup exo player
        videoSurfaceView = PlayerView(ctx)
        videoSurfaceView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        videoPlayer = SimpleExoPlayer.Builder(ctx).build()
        videoSurfaceView.useController = false
        videoSurfaceView.player = videoPlayer


        // listen for scrolling events on recycler view and play the video when scrolling stops
        // if any video is playing prior to scrolling then keep playing that video until scrolling
        // stops to change video
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    if(thumbnail != null){
                        thumbnail!!.visibility = VISIBLE
                    }
                    if(progressBar != null){
                        progressBar!!.visibility = GONE
                    }

                    // check if page reached end if yes play end video
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true)
                    }else {
                        playVideo(false)
                    }
                }
            }

        })


        // this piece of code decides what to do when view gets attached to parent
        addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
//                TODO("Not yet implemented")
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                if (viewHolderParent != null && viewHolderParent!! == view) {
                    resetVideoView()
                }
            }
        })


    }

    private fun playVideo(isAtEnd: Boolean) {
        var targetPosition = -1
        if (!isAtEnd) {
            val startPosition = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            var endPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()

            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1
            }

            // error
            if (startPosition < 0 || endPosition < 0) {
                return
            }

            targetPosition = if (startPosition != endPosition) {
                val startPositionVideoHeight: Int = getVisibleVideoSurfaceHeight(startPosition)
                val endPositionVideoHeight: Int = getVisibleVideoSurfaceHeight(endPosition)
                if (startPositionVideoHeight > endPositionVideoHeight) {
                    startPosition
                } else {
                    endPosition
                }
            } else {
                startPosition
            }

        } else {
            targetPosition = (videoList!!.size) - 1
        }

        if (targetPosition == playPosition) {
            return
        }
        playPosition = targetPosition

        Log.d("playPosition", "$playPosition")

        videoSurfaceView.visibility = INVISIBLE
        removeVideoView(videoSurfaceView)

        val currentPosition =
            targetPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        Log.d("currentPosition","$currentPosition")

        // Bind data to view to perform play video
        val child = getChildAt(currentPosition) ?: return
        val holder = child.tag as VideoPlayerViewHolder
        thumbnail = holder.thumbnail
        progressBar = holder.progressBar
        soundIndicator = holder.itemView.findViewById(R.id.soundIndicator)
        retryBtn = holder.itemView.findViewById(R.id.retryImg)
        requestManager = holder.requestManager
        frameLayout = holder.itemView.findViewById(R.id.media_container)
        progressBar?.visibility= VISIBLE

        // Exo player data binding code starts
        videoSurfaceView.player = videoPlayer
        val mediaUrl: String = videoList!![targetPosition].sources
        val mediaSource = buildMediaSource(mediaUrl)
        videoPlayer!!.playWhenReady = true
        videoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
        videoPlayer!!.seekTo(playbackPosition)
        videoPlayer!!.setMediaSource(mediaSource)
        playBackEventListener()
        videoPlayer!!.prepare()


        //handle tap event to mute and un-mute playing video
        holder.itemView.setOnClickListener {
            if(!isMuted){
                videoPlayer!!.volume = 0f
                isMuted = true
                animateVolumeControl()
            }else{
                videoPlayer!!.volume = 1f
                isMuted = false
                animateVolumeControl()
            }
        }

    }

    // check for video playing state (self explanatory function)
    private fun playBackEventListener() {
        videoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    Player.STATE_BUFFERING -> {
                        Log.d("VideoPlay", "STATE_BUFFERING")
                        retryBtn.visibility = GONE
                        progressBar!!.visibility = VISIBLE
                    }
                    Player.STATE_ENDED -> {
                        Log.d("VideoPlay", "STATE_ENDED")
                    }
                    Player.STATE_IDLE -> {
                        Log.d("VideoPlay", "STATE_IDLE")
                        // show retry button on video load failure
                        if(progressBar?.visibility == VISIBLE){
                            progressBar!!.visibility = GONE
                        }
                        retryBtn.visibility = VISIBLE
                        retryBtn.setOnClickListener {
                            videoPlayer!!.prepare()
                        }
                    }
                    Player.STATE_READY -> {
                        Log.d("VideoPlay", "STATE_READY")
                        progressBar!!.visibility = GONE
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                    }
                }
            }
        })
    }


    private fun buildMediaSource(uri: String): MediaSource {
        // Create a data source factory.
        val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
        // Create a progressive media source pointing to a stream uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    private fun addVideoView() {
        frameLayout?.addView(videoSurfaceView)
        isVideoViewAdded = true
        videoSurfaceView.requestFocus()
        videoSurfaceView.visibility = VISIBLE
        videoSurfaceView.alpha = 1f
        thumbnail?.visibility = GONE
    }

    private fun removeVideoView(videoView: PlayerView) {
        val parent = videoView.parent as ViewGroup?
        val index = parent?.indexOfChild(videoView)
        if (index != null) {
            if (index >= 0) {
                parent.removeViewAt(index)
                isVideoViewAdded = false
                videoSurfaceView.setOnClickListener(null)
            }
        }
    }

    private fun resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView)
            playPosition = -1
            videoSurfaceView.visibility = INVISIBLE
            thumbnail?.visibility = VISIBLE
        }
    }

    fun releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer!!.release()
            videoPlayer = null
        }
        viewHolderParent = null
    }

    fun pausePlayer(){
        videoPlayer!!.pause()
    }

    // Determine playing video height
    // Assumption :- screen hold two video only, even if there are more than two code ensures,
    // it remains two for deciding which one to play
    private fun getVisibleVideoSurfaceHeight(playPosition: Int): Int {
        val at =
            playPosition - (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val child = getChildAt(at) ?: return 0

        val location = IntArray(2)
        child.getLocationInWindow(location)

        return if (location[1] < 0) {
            location[1] + videoSurfaceDefaultHeight
        } else {
            screenDefaultHeight - location[1]
        }
    }

    // just to animate sound indicator.
    private fun animateVolumeControl() {
        soundIndicator.bringToFront()
        if (isMuted) {
            requestManager.load(R.drawable.ic_baseline_volume_off_24)
                .into(soundIndicator)
        } else if (!isMuted) {
            requestManager.load(R.drawable.ic_baseline_volume_up_24)
                .into(soundIndicator)
        }
        soundIndicator.animate().cancel()
        soundIndicator.alpha = 1f

        soundIndicator.animate().alpha(0f).setDuration(600).startDelay = 1000
    }

    fun resumePlayer() {
        videoPlayer!!.play()
    }

}

