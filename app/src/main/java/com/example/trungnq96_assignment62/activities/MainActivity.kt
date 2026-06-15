package com.example.trungnq96_assignment62.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.RVMainAdapter
import com.example.trungnq96_assignment62.databinding.ActivityMainBinding
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.entities.Song
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    var musicService: MusicService? = null
    var isBound = false

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            binder: IBinder?
        ) {
            musicService = (binder as MusicService.MusicBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvMain.also {
            it.layoutManager = LinearLayoutManager(this@MainActivity)
            it.adapter = RVMainAdapter(
                ArrayList(ListsObject.listSongs),
                ArrayList(ListsObject.listArtists),
                this@MainActivity
            ) { song ->
                if (isBound) {
                    val audioResId = resources.getIdentifier(song.audioUrl, "raw", packageName)
                    if (audioResId != 0) {
                        musicService?.playSong(audioResId)
                        
                        // Đồng bộ Fragment MiniPlayer
                        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? com.example.trungnq96_assignment62.fragments.MiniPlayerFragment
                        
                        // Tìm tên nghệ sĩ để hiển thị
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
                        
                        fragment?.updateSongInfo(song, artistName)
                    }
                }
            }
        }

        val intent = Intent(this@MainActivity, MusicService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }


}