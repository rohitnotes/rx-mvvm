package com.example.android.databinding.basicsample.handler

import android.content.Context
import android.content.Intent
import com.example.android.databinding.basicsample.ui.detailmovie.DetailMovieActivity
import com.example.android.databinding.basicsample.ui.detailtvshow.DetailTVShowActivity

open class EventHandler(context: Context) {

    val mContext: Context = context


    fun onMovieDetailsClick(id: String) {
        val intent = Intent(mContext, DetailMovieActivity::class.java)
        intent.putExtra("data", id)
        mContext.startActivity(intent)
    }

    fun onTvShowDetailsClick(id: String) {
        val intent = Intent(mContext, DetailTVShowActivity::class.java)
        intent.putExtra("data", id)
        mContext.startActivity(intent)
    }

}