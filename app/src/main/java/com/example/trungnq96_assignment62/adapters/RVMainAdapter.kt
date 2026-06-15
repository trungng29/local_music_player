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
import com.example.trungnq96_assignment62.activities.SongActivity
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.entities.Song

class RVMainAdapter(
    val listSongs: MutableList<Song>,
    val listArtists: MutableList<Artist>,
    val context: Context,
    private val onItemClick: (Song) -> Unit
): RecyclerView.Adapter<RVMainAdapter.ViewHolder>() {
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        val cvSong: CardView = itemView.findViewById(R.id.cvSong)
    }

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.card_view, p0, false))
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val resId: Int = context.resources.getIdentifier(
            listSongs[p1].coverUrl, "drawable", context.packageName
        )

        p0.ivCover.setImageResource(resId)
        p0.tvTitle.text = listSongs[p1].title
        listArtists.forEach { it1 ->
            if (it1.singles.contains(listSongs[p1])) {
                p0.tvArtist.text = it1.name
                return@forEach
            }
            it1.albums.forEach { album ->
                album.songs.forEach { song ->
                    if (song.title == listSongs[p1].title) {
                        p0.tvArtist.text = it1.name
                        return@forEach
                    }
                }
            }
        }
        p0.cvSong.setOnClickListener {
            onItemClick(listSongs[p1])
            val intent = Intent(context, SongActivity::class.java).also {
                it.putExtra("cover", resId)
                it.putExtra("name", listSongs[p1].title)
                it.putExtra("author", p0.tvArtist.text)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listSongs.size
    }
}