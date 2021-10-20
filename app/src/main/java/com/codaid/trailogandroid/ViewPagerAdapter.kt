package com.codaid.trailogandroid

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.codaid.trailogandroid.main.dash_board.TrainingFragment
import com.codaid.trailogandroid.main.dash_board.WeightFragment
import com.codaid.trailogandroid.main.dash_board.WorkoutFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    val titleIds =
        listOf(R.string.tab_title_weight, R.string.tab_title_training, R.string.tab_title_workout)
    private val fragments = listOf(WeightFragment(), TrainingFragment(), WorkoutFragment())

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}