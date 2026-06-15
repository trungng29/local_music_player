package com.example.trungnq96_assignment62.entities

data class Album(
    val title: String,
    val coverUrl: String,
    val songs: MutableList<Song>
) {
}