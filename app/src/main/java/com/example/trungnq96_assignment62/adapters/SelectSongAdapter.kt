package com.example.trungnq96_assignment62.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.entities.Song

class SelectSongAdapter(
    var songs: List<Song>,
    val onSelectionChanged: (Song, Boolean) -> Unit
) : RecyclerView.Adapter<SelectSongAdapter.ViewHolder>() {

    val selectedSongs = mutableSetOf<Song>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(android.R.id.text1)
        val checkBox: CheckBox = CheckBox(itemView.context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using a simple list item layout from Android for simplicity or creating a small one
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.title
        
        val listView = holder.itemView as android.widget.CheckedTextView
        listView.isChecked = selectedSongs.contains(song)

        holder.itemView.setOnClickListener {
            if (selectedSongs.contains(song)) {
                selectedSongs.remove(song)
                listView.isChecked = false
                onSelectionChanged(song, false)
            } else {
                selectedSongs.add(song)
                listView.isChecked = true
                onSelectionChanged(song, true)
            }
        }
    }

    override fun getItemCount(): Int = songs.size

    fun updateList(newList: List<Song>) {
        songs = newList
        notifyDataSetChanged()
    }
}