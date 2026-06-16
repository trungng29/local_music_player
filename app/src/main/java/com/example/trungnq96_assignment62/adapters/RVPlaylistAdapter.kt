package com.example.trungnq96_assignment62.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trungnq96_assignment62.R
import android.content.Intent
import com.example.trungnq96_assignment62.activities.PlaylistActivity
import com.example.trungnq96_assignment62.entities.Playlist

class RVPlaylistAdapter(
    val listPlaylists: MutableList<Playlist>,
    val context: Context,
    val onItemClick: (Playlist) -> Unit
): RecyclerView.Adapter<RVPlaylistAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvPlaylistName: TextView = itemView.findViewById(R.id.tvTitle)
        val tvSongCount: TextView = itemView.findViewById(R.id.tvArtist)
        // We hide the image view in onBind
        val ivCover: View = itemView.findViewById(R.id.ivCover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = listPlaylists[position]
        holder.tvPlaylistName.text = playlist.name
        holder.tvSongCount.text = "${playlist.songs.size} songs"
        holder.ivCover.visibility = View.GONE
        
        holder.itemView.setOnClickListener {
            onItemClick(playlist)
            val intent = Intent(context, PlaylistActivity::class.java).also {
                it.putExtra("playlist_name", playlist.name)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listPlaylists.size
}