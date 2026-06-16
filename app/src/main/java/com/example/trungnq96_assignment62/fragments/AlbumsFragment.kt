package com.example.trungnq96_assignment62.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trungnq96_assignment62.adapters.RVAlbumAdapter
import com.example.trungnq96_assignment62.databinding.FragmentTabListBinding
import com.example.trungnq96_assignment62.objects.ListsObject

class AlbumsFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val allAlbums = ListsObject.listArtists.flatMap { it.albums }.toMutableList()
        
        binding.rvTabList.layoutManager = LinearLayoutManager(context)
        binding.rvTabList.adapter = RVAlbumAdapter(
            allAlbums,
            ArrayList(ListsObject.listArtists),
            requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}