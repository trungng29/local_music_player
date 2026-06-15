package com.example.trungnq96_assignment62.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.RVMainAdapter
import com.example.trungnq96_assignment62.databinding.ActivityArtistBinding
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.objects.ListsObject

class ArtistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtistBinding

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
        Log.d("name", name.toString())
        lateinit var artist: Artist

        ListsObject.listArtists.forEach {
            if (it.name == name) {
                artist = it
            }
        }

        binding.ivCoverArtistScreen.setImageResource(resources.getIdentifier(artist.avatarUrl, "drawable", this.packageName))
        binding.tvArtistNameArtistScreen.text = artist.name

        val songs = artist.singles.toMutableList()
        artist.albums.forEach { album ->
            songs.addAll(album.songs)
        }

        val count = minOf(songs.size, 5)

        binding.rvSongsArtistPage.also {
            it.layoutManager = LinearLayoutManager(this@ArtistActivity)
            it.adapter = RVMainAdapter(songs.take(count).toMutableList(), mutableListOf(artist), this@ArtistActivity) {
            }
            it.isNestedScrollingEnabled = false
        }

        binding.rvSongsArtistPage.layoutParams.height =
            (count * 100 * resources.displayMetrics.density).toInt()

        binding.rvAlbumsArtistPage.also {
            it.layoutManager = LinearLayoutManager(this@ArtistActivity)
            it.adapter = RVMainAdapter(artist.albums.toMutableList(), mutableListOf(artist), this@ArtistActivity) {
            }
            it.isNestedScrollingEnabled = false
        }

    }
}