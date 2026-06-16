package com.example.trungnq96_assignment62.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.databinding.FragmentMiniPlayerBinding
import com.example.trungnq96_assignment62.services.MusicService

class MiniPlayerFragment : Fragment() {
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!

    private var musicService: MusicService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    private val stateListener: () -> Unit = {
        activity?.runOnUiThread {
            updateUI()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiniPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView9.setOnClickListener {
            if (isBound) {
                if (MusicService.isPlaying) {
                    musicService?.pause()
                } else {
                    musicService?.resume()
                }
            }
        }
        
        MusicService.addListener(stateListener)
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

    private fun updateUI() {
        if (_binding == null) return
        val song = MusicService.currentSong
        val artist = MusicService.currentArtist
        val isPlaying = MusicService.isPlaying

        if (song != null) {
            binding.root.visibility = View.VISIBLE
            binding.textView7.text = song.title
            binding.textView8.text = artist
            
            val resId = resources.getIdentifier(song.coverUrl, "drawable", requireContext().packageName)
            if (resId != 0) {
                binding.ivCoverMiniPlayer.setImageResource(resId)
            }
            
            binding.imageView9.setImageResource(
                if (isPlaying) R.drawable.pause_circle_24dp else R.drawable.play_circle_24dp
            )
        } else {
            binding.root.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MusicService.removeListener(stateListener)
        _binding = null
    }
}