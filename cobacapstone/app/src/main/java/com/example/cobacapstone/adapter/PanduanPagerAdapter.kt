package com.example.cobacapstone.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cobacapstone.ui.fragment.TeksProsedurFragment
import com.example.cobacapstone.ui.fragment.VideoFragment

class PanduanPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TeksProsedurFragment()
            1 -> VideoFragment()
            else -> throw IllegalStateException("Invalid position")
        }
    }
}
