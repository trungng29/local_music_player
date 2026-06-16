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
import com.example.trungnq96_assignment62.objects.ListsObject

class MusicService : Service() {

    companion object {
        const val CHANNEL_ID = "music_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_PAUSE = "MUSIC_PAUSE"
        const val ACTION_PLAY = "MUSIC_PLAY"
        const val ACTION_CLOSE = "MUSIC_CLOSE"
        const val ACTION_NEXT = "MUSIC_NEXT"
        const val ACTION_PREVIOUS = "MUSIC_PREVIOUS"

        var currentSong: Song? = null
        var currentArtist: String = ""
        var isPlaying: Boolean = false
        var currentSongList: List<Song> = emptyList()
        
        private val listeners = mutableListOf<() -> Unit>()
        
        fun addListener(listener: () -> Unit) {
            if (!listeners.contains(listener)) {
                listeners.add(listener)
            }
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
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> pause()
            ACTION_PLAY -> resume()
            ACTION_NEXT -> next()
            ACTION_PREVIOUS -> previous()
            ACTION_CLOSE -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!isPlaying) {
            stopSelf()
        }
        super.onTaskRemoved(rootIntent)
    }

    private fun updateNotification() {
        val channel = NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationLayout = RemoteViews(packageName, R.layout.music_player)
        val notificationLayoutBig = RemoteViews(packageName, R.layout.music_player_big)
        
        val title = currentSong?.title ?: "Unknown"
        val artist = currentArtist
        val resIdCover = currentSong?.let { resources.getIdentifier(it.coverUrl, "drawable", packageName) } ?: 0
        
        val playPauseAction = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
        val playPauseIcon = if (isPlaying) R.drawable.pause_circle_24dp else R.drawable.play_circle_24dp
        
        val pPlayPause = PendingIntent.getService(this, 0, Intent(this, MusicService::class.java).apply { action = playPauseAction }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pClose = PendingIntent.getService(this, 1, Intent(this, MusicService::class.java).apply { action = ACTION_CLOSE }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pNext = PendingIntent.getService(this, 2, Intent(this, MusicService::class.java).apply { action = ACTION_NEXT }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pPrev = PendingIntent.getService(this, 3, Intent(this, MusicService::class.java).apply { action = ACTION_PREVIOUS }, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pMain = PendingIntent.getActivity(this, 4, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

        listOf(notificationLayout, notificationLayoutBig).forEach { layout ->
            layout.setTextViewText(R.id.textView, title)
            layout.setTextViewText(R.id.textView2, artist)
            layout.setImageViewResource(R.id.ivPause, playPauseIcon)
            layout.setOnClickPendingIntent(R.id.ivPause, pPlayPause)
            layout.setOnClickPendingIntent(R.id.ivNext, pNext)
            layout.setOnClickPendingIntent(R.id.ivPrev, pPrev)
        }

        notificationLayoutBig.setOnClickPendingIntent(R.id.ivClose, pClose)
        if (resIdCover != 0) {
            notificationLayoutBig.setImageViewResource(R.id.imageView7, resIdCover)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutBig)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pMain)
            .setOngoing(isPlaying)
            .setSilent(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    fun playSong(song: Song, artistName: String, songList: List<Song> = emptyList()) {
        val intent = Intent(this, MusicService::class.java)
        startService(intent)

        if (songList.isNotEmpty()) {
            currentSongList = songList
        } else if (currentSongList.isEmpty()) {
            currentSongList = ListsObject.listSongs
        }

        val resId = resources.getIdentifier(song.audioUrl, "raw", packageName)
        if (resId == 0) return

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, resId)
        
        mediaPlayer?.let {
            it.start()
            it.setOnCompletionListener {
                next()
            }
            
            currentSong = song
            currentArtist = artistName
            isPlaying = true
            notifyStateChanged()
            updateNotification()
        }
    }

    fun next() {
        if (currentSongList.isEmpty()) return
        var currentIndex = currentSongList.indexOf(currentSong)
        currentIndex = (currentIndex + 1) % currentSongList.size
        
        val nextSong = currentSongList[currentIndex]
        val artistName = findArtistForSong(nextSong)
        playSong(nextSong, artistName)
    }

    fun previous() {
        if (currentSongList.isEmpty()) return
        var currentIndex = currentSongList.indexOf(currentSong)
        currentIndex = if (currentIndex <= 0) currentSongList.size - 1 else currentIndex - 1
        
        val prevSong = currentSongList[currentIndex]
        val artistName = findArtistForSong(prevSong)
        playSong(prevSong, artistName)
    }

    private fun findArtistForSong(song: Song): String {
        ListsObject.listArtists.forEach { artist ->
            if (artist.singles.contains(song)) return artist.name
            artist.albums.forEach { album ->
                if (album.songs.any { it.title == song.title }) return artist.name
            }
        }
        return "Unknown"
    }

    fun pause() {
        mediaPlayer?.pause()
        isPlaying = false
        notifyStateChanged()
        updateNotification()
    }

    fun resume() {
        mediaPlayer?.start()
        isPlaying = true
        notifyStateChanged()
        updateNotification()
    }

    fun isServicePlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

}