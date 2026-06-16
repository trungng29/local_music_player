package com.example.trungnq96_assignment62.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.adapters.RVMainAdapter
import com.example.trungnq96_assignment62.databinding.FragmentTabListBinding
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService

class SongsFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvTabList.layoutManager = LinearLayoutManager(context)
        binding.rvTabList.adapter = RVMainAdapter(
            ArrayList(ListsObject.listSongs),
            ArrayList(ListsObject.listArtists),
            requireContext()
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
                musicService?.playSong(song, artistName, ListsObject.listSongs)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(requireContext(), MusicService::class.java).also { intent ->
            requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            requireContext().unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}