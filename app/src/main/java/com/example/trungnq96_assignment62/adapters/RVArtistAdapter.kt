package com.example.trungnq96_assignment62.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.activities.ArtistActivity
import com.example.trungnq96_assignment62.entities.Artist

class RVArtistAdapter(
    val listArtists: MutableList<Artist>,
    val context: Context
): RecyclerView.Adapter<RVArtistAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val cvSong: CardView = itemView.findViewById(R.id.cvSong)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val artist = listArtists[position]
        val resId = context.resources.getIdentifier(artist.avatarUrl, "drawable", context.packageName)

        holder.ivCover.setImageResource(resId)
        holder.tvTitle.text = artist.name
        val songCount = artist.singles.size + artist.albums.sumOf { it.songs.size }
        holder.tvArtist.text = "$songCount songs"

        holder.cvSong.setOnClickListener {
            val intent = Intent(context, ArtistActivity::class.java).also {
                it.putExtra("artist_name", artist.name)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listArtists.size
}