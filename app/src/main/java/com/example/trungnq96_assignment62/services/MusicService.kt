package com.example.trungnq96_assignment62.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.activities.MainActivity
import com.example.trungnq96_assignment62.entities.Song

class MusicService : Service() {

    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PAUSE = "MUSIC_PAUSE"
        const val ACTION_PLAY = "MUSIC_PLAY"
        const val ACTION_CLOSE = "MUSIC_CLOSE"

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> pause()
            ACTION_PLAY -> resume()
            ACTION_CLOSE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun updateNotification() {
        val channel = NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        // Layout nhỏ
        val notificationLayout = RemoteViews(packageName, R.layout.music_player)
        // Layout lớn khi expand
        val notificationLayoutBig = RemoteViews(packageName, R.layout.music_player_big)
        
        val title = currentSong?.title ?: "Unknown"
        val artist = currentArtist
        val resIdCover = currentSong?.let { resources.getIdentifier(it.coverUrl, "drawable", packageName) } ?: 0
        
        val playPauseAction = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        val playPauseIcon = if (isPlaying) R.drawable.pause_circle_24dp else R.drawable.play_circle_24dp
        
        val playPauseIntent = Intent(this, MusicService::class.java).apply { action = playPauseAction }
        val pPlayPause = PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        
        val closeIntent = Intent(this, MusicService::class.java).apply { action = ACTION_CLOSE }
        val pClose = PendingIntent.getService(this, 1, closeIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val mainIntent = Intent(this, MainActivity::class.java)
        val pMain = PendingIntent.getActivity(this, 2, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        // Setup layout nhỏ
        notificationLayout.setTextViewText(R.id.textView, title)
        notificationLayout.setTextViewText(R.id.textView2, artist)
        notificationLayout.setImageViewResource(R.id.ivPause, playPauseIcon)
        notificationLayout.setOnClickPendingIntent(R.id.ivPause, pPlayPause)
        notificationLayout.setOnClickPendingIntent(R.id.ivNext, pClose)

        // Setup layout lớn
        notificationLayoutBig.setTextViewText(R.id.textView, title)
        notificationLayoutBig.setTextViewText(R.id.textView2, artist)
        notificationLayoutBig.setImageViewResource(R.id.ivPause, playPauseIcon)
        notificationLayoutBig.setOnClickPendingIntent(R.id.ivPause, pPlayPause)
        notificationLayoutBig.setOnClickPendingIntent(R.id.ivClose, pClose)
        if (resIdCover != 0) {
            notificationLayoutBig.setImageViewResource(R.id.imageView7, resIdCover)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutBig) // Sử dụng layout music_player_big
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pMain)
            .setOngoing(isPlaying)
            .setSilent(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
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
                updateNotification()
            }
        }
        currentSong = song
        currentArtist = artistName
        isPlaying = true
        notifyStateChanged()
        updateNotification()
    }

    fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        isPlaying = false
        notifyStateChanged()
        updateNotification()
    }

    fun resume() {
        mediaPlayer.start()
        isPlaying = true
        notifyStateChanged()
        updateNotification()
    }

    fun isServicePlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

}