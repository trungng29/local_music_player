package com.example.trungnq96_assignment62.entities

data class Artist(
    val name: String,
    val avatarUrl: String,
    val albums: MutableList<Album>,
    val singles: MutableList<Song>
) {

}