package com.example.trungnq96_assignment62.activities

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trungnq96_assignment62.R
import com.example.trungnq96_assignment62.adapters.MainPagerAdapter
import com.example.trungnq96_assignment62.databinding.ActivityMainBinding
import com.example.trungnq96_assignment62.objects.ListsObject
import com.example.trungnq96_assignment62.services.MusicService
import com.google.android.material.tabs.TabLayoutMediator

const val REQUEST_CODE = 100

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

        setupViewPager()

        // Load Playlists từ bộ nhớ điện thoại khi mở app
        ListsObject.loadPlaylists(this)

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

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}