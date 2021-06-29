package com.storm.feedvideoplayer.util

import android.content.Context
import com.storm.feedvideoplayer.data.VideoDetailModel
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

val videoList:List<VideoDetailModel> = mutableListOf()


fun Context.jsonFileToList(fileName: String): List<VideoDetailModel>?{
    val jsonString: String
    val videoList = mutableListOf<VideoDetailModel>()
    try {
        lateinit var videoDetailModel : VideoDetailModel
        jsonString = this.assets.open(fileName).bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()){
            val jsonObj = jsonArray.getJSONObject(i)
            videoDetailModel = VideoDetailModel(
                jsonObj.getString("description"),
                jsonObj.getString("sources"),
                jsonObj.getString("subtitle"),
                jsonObj.getString("thumb"),
                jsonObj.getString("title"),
            )
            videoList.add(videoDetailModel)
        }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return videoList
}
