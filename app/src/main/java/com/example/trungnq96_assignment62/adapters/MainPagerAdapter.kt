package com.example.trungnq96_assignment62.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.trungnq96_assignment62.fragments.AlbumsFragment
import com.example.trungnq96_assignment62.fragments.ArtistsFragment
import com.example.trungnq96_assignment62.fragments.PlaylistsFragment
import com.example.trungnq96_assignment62.fragments.SongsFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SongsFragment()
            1 -> AlbumsFragment()
            2 -> ArtistsFragment()
            3 -> PlaylistsFragment()
            else -> SongsFragment()
        }
    }
}