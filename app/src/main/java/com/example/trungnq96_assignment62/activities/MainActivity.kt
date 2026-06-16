package com.example.trungnq96_assignment62.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.widget.RemoteViews
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.MainPagerAdapter
import com.example.trungnq96_assignment62.databinding.ActivityMainBinding
import com.example.trungnq96_assignment62.services.MusicService
import com.google.android.material.tabs.TabLayoutMediator

const val CHANNEL_ID = "1"
const val REQUEST_CODE = 100
const val NOTIFICATION_ID = 1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var permission = arrayOf(Manifest.permission.POST_NOTIFICATIONS)

    var musicService: MusicService? = null
    var isBound = false

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            musicService = (binder as MusicService.MusicBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permission, REQUEST_CODE)
        }

        buildNotification()
        setupViewPager()

        val intent = Intent(this@MainActivity, MusicService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Songs"
                1 -> "Albums"
                2 -> "Artists"
                3 -> "Playlist"
                else -> ""
            }
        }.attach()
    }

    private fun buildNotification() {
        val channel = NotificationChannel(CHANNEL_ID, "Music Notification", NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val remoteView = RemoteViews(packageName, R.layout.music_player)
        val remoteViewBig = RemoteViews(packageName, R.layout.music_player_big)

        val pauseIntent = PendingIntent.getBroadcast(this, 0, Intent("PAUSE").setPackage(packageName), PendingIntent.FLAG_IMMUTABLE)
        val nextIntent = PendingIntent.getBroadcast(this, 1, Intent("SKIP_NEXT").setPackage(packageName), PendingIntent.FLAG_IMMUTABLE)
        val previousIntent = PendingIntent.getBroadcast(this, 2, Intent("SKIP_PREVIOUS").setPackage(packageName), PendingIntent.FLAG_IMMUTABLE)
        val closeIntent = PendingIntent.getBroadcast(this, 3, Intent("CLOSE").setPackage(packageName), PendingIntent.FLAG_IMMUTABLE)

        remoteView.setOnClickPendingIntent(R.id.ivPause, pauseIntent)
        remoteView.setOnClickPendingIntent(R.id.ivNext, nextIntent)
        remoteView.setOnClickPendingIntent(R.id.ivPrev, previousIntent)
        remoteView.setOnClickPendingIntent(R.id.ivClose, closeIntent)

        remoteViewBig.setOnClickPendingIntent(R.id.ivPause, pauseIntent)
        remoteViewBig.setOnClickPendingIntent(R.id.ivNext, nextIntent)
        remoteViewBig.setOnClickPendingIntent(R.id.ivPrev, previousIntent)
        remoteViewBig.setOnClickPendingIntent(R.id.ivClose, closeIntent)

        val builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(remoteView)
            .setCustomBigContentView(remoteViewBig)

    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}