package com.example.trungnq96_assignment62.objects

import androidx.appcompat.R
import com.example.trungnq96_assignment62.entities.Album
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.entities.Playlist
import com.example.trungnq96_assignment62.entities.Song

object ListsObject {
    val listSongs: MutableList<Song> = ArrayList<Song>()
    val listArtists: MutableList<Artist> = ArrayList<Artist>()
    val listPlaylists: MutableList<Playlist> = ArrayList<Playlist>()

    init {
        listArtists.add(Artist("Marzuz", "marzuz_avatar", ArrayList(), ArrayList()).also {
            it.albums.add(Album("ả", "a_marzuz", ArrayList()).also { it1 ->
                it1.songs.add(Song("ả", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("ngăn cản", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("đang yên đang lành", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("ảo ảnh", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("rêu xanh trên mặt nước", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("cảm ơn?", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("thiên thần giáng trần", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("phản chiếu", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })

                it1.songs.add(Song("lại là cảm xúc này phải không?", "a_marzuz", "sample").also { it2 ->
                    listSongs.add(it2)
                })
            })
        })

    }

}
