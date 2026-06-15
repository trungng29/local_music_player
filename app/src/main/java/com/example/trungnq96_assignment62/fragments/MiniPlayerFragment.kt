package com.example.trungnq96_assignment62.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.activities.MainActivity
import com.example.trungnq96_assignment62.activities.SongActivity
import com.example.trungnq96_assignment62.databinding.FragmentMiniPlayerBinding
import com.example.trungnq96_assignment62.entities.Song

class MiniPlayerFragment : Fragment() {
    private var _binding: FragmentMiniPlayerBinding? = null
    private val binding get() = _binding!!

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
            val mainActivity = activity as? MainActivity
            val songActivity = activity as? SongActivity
            if ((mainActivity != null && mainActivity.isBound) || (songActivity != null && songActivity.isBound)) {
                val serviceMain = mainActivity?.musicService
                val serviceSong = songActivity?.musicService
                if (serviceMain != null || serviceSong != null) {
                    if (serviceSong?.isPlaying() == true || serviceMain?.isPlaying() == true) {
                        serviceSong?.pause()
                        serviceMain?.pause()
                        binding.imageView9.setImageResource(R.drawable.play_circle_24dp)
                    } else {
                        serviceSong?.resume()
                        serviceMain?.resume()
                        binding.imageView9.setImageResource(R.drawable.pause_circle_24dp)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun updateSongInfo(song: Song, artistName: String) {
        binding.textView7.text = song.title
        binding.textView8.text = artistName
        
        val resId = resources.getIdentifier(song.coverUrl, "drawable", requireContext().packageName)
        if (resId != 0) {
            // Dùng viewById nếu Binding chưa nhận diện đúng ID
            val ivProfile = view?.findViewById<android.widget.ImageView>(R.id.ivCoverMiniPlayer)
            ivProfile?.setImageResource(resId)
        }

        binding.imageView9.setImageResource(R.drawable.pause_circle_24dp)
    }
}