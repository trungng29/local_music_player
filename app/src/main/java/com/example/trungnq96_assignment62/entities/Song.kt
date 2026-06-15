package com.example.trungnq96_assignment62.entities

data class Song(
    val title: String,
    val coverUrl: String,
    val audioUrl: String,
    val duration: Int = 0
) {
}