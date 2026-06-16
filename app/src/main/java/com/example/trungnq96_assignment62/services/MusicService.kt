package com.example.trungnq96_assignment62.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.example.trungnq96_assignment62.entities.Song

class MusicService : Service() {

    companion object {
        var currentSong: Song? = null
        var currentArtist: String = ""
        var isPlaying: Boolean = false
        
        private val listeners = mutableListOf<() -> Unit>()
        
        fun addListener(listener: () -> Unit) {
            listeners.add(listener)
            listener()
        }
        
        fun removeListener(listener: () -> Unit) {
            listeners.remove(listener)
        }
        
        private fun notifyStateChanged() {
            listeners.forEach { it() }
        }
    }

    inner class MusicBinder: Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private val binder = MusicBinder()

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun playSong(song: Song, artistName: String) {
        val resId = resources.getIdentifier(song.audioUrl, "raw", packageName)
        if (resId == 0) return

        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(this, resId).also {
            it.start()
            it.setOnCompletionListener {
                isPlaying = false
                notifyStateChanged()
            }
        }
        currentSong = song
        currentArtist = artistName
        isPlaying = true
        notifyStateChanged()
    }

    fun pause() {
        mediaPlayer.pause()
        isPlaying = false
        notifyStateChanged()
    }

    fun resume() {
        mediaPlayer.start()
        isPlaying = true
        notifyStateChanged()
    }

    fun isServicePlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}