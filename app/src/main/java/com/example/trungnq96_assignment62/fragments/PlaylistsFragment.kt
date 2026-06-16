package com.example.trungnq96_assignment62.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.RVPlaylistAdapter
import com.example.trungnq96_assignment62.adapters.SelectSongAdapter
import com.example.trungnq96_assignment62.databinding.FragmentTabListBinding
import com.example.trungnq96_assignment62.entities.Playlist
import com.example.trungnq96_assignment62.entities.Song
import com.example.trungnq96_assignment62.objects.ListsObject

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentTabListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.fabAddPlaylist.visibility = View.VISIBLE
        binding.rvTabList.layoutManager = LinearLayoutManager(context)
        updateAdapter()

        binding.fabAddPlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }
    }

    private fun updateAdapter() {
        binding.rvTabList.adapter = RVPlaylistAdapter(ListsObject.listPlaylists, requireContext()) { playlist ->
            // Optionally open PlaylistActivity
        }
    }

    private fun showCreatePlaylistDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("New Playlist")
        
        val input = EditText(requireContext())
        input.hint = "Playlist Name"
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val name = input.text.toString()
            if (name.isNotEmpty()) {
                showAddSongsWithSearchDialog(name)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    private fun showAddSongsWithSearchDialog(playlistName: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_songs, null)
        val etSearch = dialogView.findViewById<EditText>(R.id.etSearchSongs)
        val rvSongs = dialogView.findViewById<RecyclerView>(R.id.rvSelectSongs)
        
        val selectedSongs = mutableSetOf<Song>()
        val adapter = SelectSongAdapter(ListsObject.listSongs) { song, isSelected ->
            if (isSelected) selectedSongs.add(song) else selectedSongs.remove(song)
        }
        
        rvSongs.layoutManager = LinearLayoutManager(requireContext())
        rvSongs.adapter = adapter
        
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtered = ListsObject.listSongs.filter { it.title.contains(s.toString(), ignoreCase = true) }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        AlertDialog.Builder(requireContext())
            .setTitle("Add Songs to $playlistName")
            .setView(dialogView)
            .setPositiveButton("Done") { _, _ ->
                val newPlaylist = Playlist(playlistName)
                newPlaylist.songs.addAll(selectedSongs)
                ListsObject.listPlaylists.add(newPlaylist)
                updateAdapter()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}