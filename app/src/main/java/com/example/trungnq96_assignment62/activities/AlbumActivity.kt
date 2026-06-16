package com.example.trungnq96_assignment62.activities

import android.content.ComponentName
import android.content.Context
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
import com.example.trungnq96_assignment62.databinding.ActivityAlbumBinding
import com.example.trungnq96_assignment62.entities.Album
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService

class AlbumActivity : AppCompatActivity() {

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
        binding = ActivityAlbumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val authorName = intent.getStringExtra("author")
        val albumName = intent.getStringExtra("name")
        
        var artistMain: Artist? = null
        var albumMain: Album? = null

        ListsObject.listArtists.forEach { artist ->
            if (artist.name == authorName) {
                artistMain = artist
                return@forEach
            }
        }

        artistMain?.albums?.forEach { album ->
            if (album.title == albumName) {
                albumMain = album
                return@forEach
            }
        }

        if (artistMain != null && albumMain != null) {
            binding.rvSongsAlbumPage.also {
                it.layoutManager = LinearLayoutManager(this@AlbumActivity)
                it.adapter = RVMainAdapter(albumMain!!.songs.toMutableList(), arrayListOf(artistMain!!), this@AlbumActivity) { song ->
                    if (isBound) {
                        musicService?.playSong(song, artistMain!!.name, albumMain!!.songs)
                    }
                }
            }
        }

        binding.ivAlbumCoverAlbumPage.setImageResource(intent.getIntExtra("cover", R.drawable.a_marzuz))
        binding.tvAlbumAlbumPage.text = albumName

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