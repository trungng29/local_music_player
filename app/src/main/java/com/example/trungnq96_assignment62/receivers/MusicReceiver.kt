package com.example.trungnq96_assignment62.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.trungnq96_assignment62.services.MusicService

class MusicReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "MUSIC_CLOSE") {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(MusicService.NOTIFICATION_ID)
            Toast.makeText(context, "Music closed", Toast.LENGTH_SHORT).show()
            return
        } else {
            val msg = when (action) {
                "MUSIC_PAUSE" -> "Music has been paused"
                "MUSIC_SKIP_NEXT" -> "Skip to next song"
                "MUSIC_SKIP_PREVIOUS" -> "Skip to previous song"
                else -> "Unknown action: $action"
            }
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

}