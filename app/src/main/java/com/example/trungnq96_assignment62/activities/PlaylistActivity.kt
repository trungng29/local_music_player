package com.example.trungnq96_assignment62.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.RVMainAdapter
import com.example.trungnq96_assignment62.databinding.ActivityAlbumBinding
import com.example.trungnq96_assignment62.entities.Playlist
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService

class PlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumBinding
    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            musicService = (service as MusicService.MusicBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Tận dụng layout của AlbumActivity
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val playlistName = intent.getStringExtra("playlist_name")
        var currentPlaylist: Playlist? = null

        ListsObject.listPlaylists.forEach {
            if (it.name == playlistName) {
                currentPlaylist = it
            }
        }

        currentPlaylist?.let { playlist ->
            binding.tvAlbumAlbumPage.text = playlist.name
            // Ẩn ảnh cover vì playlist không có ảnh
            binding.ivAlbumCoverAlbumPage.visibility = View.GONE
            
            binding.rvSongsAlbumPage.layoutManager = LinearLayoutManager(this)
            binding.rvSongsAlbumPage.adapter = RVMainAdapter(
                playlist.songs,
                ArrayList(ListsObject.listArtists),
                this
            ) { song ->
                if (isBound) {
                    var artistName = "Unknown"
                    ListsObject.listArtists.forEach { artist ->
                        if (artist.singles.contains(song)) {
                            artistName = artist.name
                            return@forEach
                        }
                        artist.albums.forEach { album ->
                            if (album.songs.contains(song)) {
                                artistName = artist.name
                                return@forEach
                            }
                        }
                    }
                    musicService?.playSong(song, artistName, playlist.songs)
                }
            }
        }

        val intentService = Intent(this, MusicService::class.java)
        bindService(intentService, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}