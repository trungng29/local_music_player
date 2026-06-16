package com.example.trungnq96_assignment62.entities

data class Playlist(
    var name: String,
    val songs: MutableList<Song> = mutableListOf()
)