package com.example.trungnq96_assignment62.objects

import android.content.Context
import android.content.SharedPreferences
import com.example.trungnq96_assignment62.entities.Album
import com.example.trungnq96_assignment62.entities.Artist
import com.example.trungnq96_assignment62.entities.Playlist
import com.example.trungnq96_assignment62.entities.Song
import org.json.JSONArray
import org.json.JSONObject

object ListsObject {
    val listSongs: MutableList<Song> = ArrayList<Song>()
    val listArtists: MutableList<Artist> = ArrayList<Artist>()
    val listPlaylists: MutableList<Playlist> = ArrayList<Playlist>()

    private const val PREFS_NAME = "MusicAppPrefs"
    private const val KEY_PLAYLISTS = "playlists"

    init {
        // Dữ liệu mẫu ban đầu (như cũ của bạn)
        listArtists.add(Artist("Marzuz", "marzuz_avatar", ArrayList(), ArrayList()).also {
            it.albums.add(Album("ả", "a_marzuz", ArrayList()).also { it1 ->
                it1.songs.add(Song("ả", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("ngăn cản", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("đang yên đang lành", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("ảo ảnh", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("rêu xanh trên mặt nước", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("cảm ơn?", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("thiên thần giáng trần", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("phản chiếu", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
                it1.songs.add(Song("lại là cảm xúc này phải không?", "a_marzuz", "sample").also { it2 -> listSongs.add(it2) })
            })
        })
    }

    // Lưu danh sách Playlists vào SharedPreferences dưới dạng JSON String
    fun savePlaylists(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonArray = JSONArray()
        
        listPlaylists.forEach { playlist ->
            val playlistJson = JSONObject()
            playlistJson.put("name", playlist.name)
            
            val songsArray = JSONArray()
            playlist.songs.forEach { song ->
                val songJson = JSONObject()
                songJson.put("title", song.title)
                songJson.put("coverUrl", song.coverUrl)
                songJson.put("audioUrl", song.audioUrl)
                songsArray.put(songJson)
            }
            playlistJson.put("songs", songsArray)
            jsonArray.put(playlistJson)
        }
        
        prefs.edit().putString(KEY_PLAYLISTS, jsonArray.toString()).apply()
    }

    // Đọc danh sách Playlists từ SharedPreferences
    fun loadPlaylists(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_PLAYLISTS, null)
        
        if (jsonString != null) {
            listPlaylists.clear()
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val playlistJson = jsonArray.getJSONObject(i)
                val name = playlistJson.getString("name")
                val songsArray = playlistJson.getJSONArray("songs")
                
                val playlist = Playlist(name)
                for (j in 0 until songsArray.length()) {
                    val songJson = songsArray.getJSONObject(j)
                    val title = songJson.getString("title")
                    val coverUrl = songJson.getString("coverUrl")
                    val audioUrl = songJson.getString("audioUrl")
                    playlist.songs.add(Song(title, coverUrl, audioUrl))
                }
                listPlaylists.add(playlist)
            }
        }
    }
}