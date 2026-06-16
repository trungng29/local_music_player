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
import com.example.trungnq96_assignment62.activities.AlbumActivity
import com.example.trungnq96_assignment62.activities.SongActivity
import com.example.trungnq96_assignment62.adapters.RVMainAdapter.ViewHolder
import com.example.trungnq96_assignment62.entities.Album
import com.example.trungnq96_assignment62.entities.Artist

class RVAlbumAdapter(
    val listAlbums: MutableList<Album>,
    val listArtists: MutableList<Artist>,
    val context: Context
): RecyclerView.Adapter<RVAlbumAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val cvSong: CardView = itemView.findViewById(R.id.cvSong)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resId: Int = context.resources.getIdentifier(
            listAlbums[position].coverUrl, "drawable", context.packageName
        )

        holder.ivCover.setImageResource(resId)
        holder.tvTitle.text = listAlbums[position].title
        listArtists.forEach { artist ->
            artist.albums.forEach { album ->
                if (album.title == listAlbums[position].title) {
                    holder.tvArtist.text = artist.name
                    return@forEach
                }
            }
        }

        holder.cvSong.setOnClickListener {
            val intent = Intent(context, AlbumActivity::class.java).also {
                it.putExtra("cover", resId)
                it.putExtra("name", listAlbums[position].title)
                it.putExtra("author", holder.tvArtist.text)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listAlbums.size
    }
}