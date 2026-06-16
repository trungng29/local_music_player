package com.example.trungnq96_assignment62.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.RVAlbumAdapter
import com.example.trungnq96_assignment62.adapters.RVMainAdapter
import com.example.trungnq96_assignment62.databinding.ActivityArtistBinding
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService

class ArtistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistBinding

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
        binding = ActivityArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("artist_name")
        var artist: Artist? = null

        ListsObject.listArtists.forEach {
            if (it.name == name) {
                artist = it
            }
        }

        artist?.let { art ->
            binding.ivCoverArtistScreen.setImageResource(resources.getIdentifier(art.avatarUrl, "drawable", this.packageName))
            binding.tvArtistNameArtistScreen.text = art.name

            val songs = art.singles.toMutableList()
            art.albums.forEach { album ->
                songs.addAll(album.songs)
            }

            val count = minOf(songs.size, 5)

            binding.rvSongsArtistPage.also {
                it.layoutManager = LinearLayoutManager(this@ArtistActivity)
                it.adapter = RVMainAdapter(songs.take(count).toMutableList(), mutableListOf(art), this@ArtistActivity) { song ->
                    if (isBound) {
                        musicService?.playSong(song, art.name)
                    }
                }
                it.isNestedScrollingEnabled = false
            }

            binding.rvSongsArtistPage.layoutParams.height =
                (count * 100 * resources.displayMetrics.density).toInt()

            binding.rvAlbumsArtistPage.also {
                it.layoutManager = LinearLayoutManager(this@ArtistActivity)
                it.adapter = RVAlbumAdapter(art.albums.toMutableList(), mutableListOf(art), this@ArtistActivity)
                it.isNestedScrollingEnabled = false
            }
        }

        val intent = Intent(this, MusicService::class.java)
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