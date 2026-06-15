package com.example.trungnq96_assignment62.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder

class MusicService : Service() {

    inner class MusicBinder: Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun playSong(resId: Int) {
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(this, resId).also {
            it.start()
        }
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun resume() {
        mediaPlayer.start()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}