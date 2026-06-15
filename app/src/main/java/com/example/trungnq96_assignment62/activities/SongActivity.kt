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
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.databinding.ActivitySongBinding
import com.example.trungnq96_assignment62.services.MusicService

class SongActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySongBinding

    var musicService: MusicService? = null
    var isBound: Boolean = false
    private val connection = object: ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            musicService = (p1 as MusicService.MusicBinder).getService()
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
        binding = ActivitySongBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.ivCoverSongPage.setImageResource(intent.getIntExtra("cover", R.drawable.ic_launcher_background))
        binding.tvSongTitleSongPage.text = intent.getStringExtra("name")
        binding.tvArtistSongPage.text = intent.getStringExtra("author")

        binding.tvArtistSongPage.setOnClickListener {
            val intent = Intent(this, ArtistActivity::class.java)
            intent.putExtra("artist_name", binding.tvArtistSongPage.text.toString())
            startActivity(intent)
        }

        val intent = Intent(this@SongActivity, MusicService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)

        binding.ivPlayPauseButton.setOnClickListener {
            if (isBound) {
                val service = musicService
                if (service != null) {
                    if (service.isPlaying()) {
                        service.pause()
                        binding.ivPlayPauseButton.setImageResource(R.drawable.play_circle_24dp)
                    } else {
                        service.resume()
                        binding.ivPlayPauseButton.setImageResource(R.drawable.pause_circle_24dp)
                    }
                }
            }
        }
    }
}