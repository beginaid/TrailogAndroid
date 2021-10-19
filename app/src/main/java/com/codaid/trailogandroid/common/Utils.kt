package com.codaid.trailogandroid.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.codaid.trailogandroid.MyApplication
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.main.add_training.AddTrainingActivity
import com.codaid.trailogandroid.main.add_weight.AddWeightActivity
import com.codaid.trailogandroid.main.add_workout.AddWorkoutActivity
import com.codaid.trailogandroid.MainActivity
import com.codaid.trailogandroid.SettingActivity
import com.codaid.trailogandroid.main.dash_board.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Utils {

    lateinit var mIntent: Intent

    companion object {
        var context = MyApplication.instance
        val navList = listOf(
        R.id.nav_dash_board,
        R.id.nav_add_weight,
        R.id.nav_add_training,
        R.id.nav_add_workout,
        R.id.nav_settings
        )
        val navTitles = listOf(
        context?.getString(R.string.menu_dash_board),
        context?.getString(R.string.menu_add_weight),
            context?.getString(R.string.menu_add_training),
            context?.getString(R.string.menu_add_workout),
            context?.getString(R.string.menu_settings)
        )
        val activityList = listOf(
        MainActivity(),
        AddWeightActivity(),
        AddTrainingActivity(),
        AddWorkoutActivity(),
        SettingActivity()
        )
        val optionList = listOf(
        R.id.action_settings
        )
        val optionActivityList = listOf(
        SettingActivity()
        )
    }

    fun goAnotherActivity(toolbar: androidx.appcompat.widget.Toolbar, mGenre: Int, loc: String) {
        toolbar.title = navTitles[mGenre]
        if (loc == "nav") {
            mIntent = Intent(context, activityList[mGenre]::class.java)
        } else if (loc == "option") {
            mIntent = Intent(context, optionActivityList[mGenre]::class.java)
        }
        context?.startActivity(mIntent)
    }

    fun createToolbar(activity: Activity, supportActionBar: ActionBar?, navView: NavigationView, drawerLayout: DrawerLayout, toolbar: androidx.appcompat.widget.Toolbar) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val sharedPref = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val email = sharedPref?.getString(context?.getString(R.string.saved_email), context?.getString(R.string.default_email))
        navView.getHeaderView(0).findViewById<TextView>(R.id.header_email).text = email
        val toggle = ActionBarDrawerToggle(activity, drawerLayout, toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun selectItem(navView: NavigationView, index: Int) {
        val navigationView = navView.menu
        navigationView.getItem(index).isChecked = true
    }

    fun createTabs(viewPager2: ViewPager2, viewPagerAdapter: ViewPagerAdapter, tabLayout: TabLayout) {
        viewPager2.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = viewPagerAdapter.itemCount
        }
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    fun setSharedPreference(): Pair<String, String> {
        val sharedPref = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val userId = sharedPref?.getString(context?.getString(R.string.saved_user_id), context?.getString(R.string.default_user_id))
            .toString()
        val email = sharedPref?.getString(context?.getString(R.string.saved_email), context?.getString(R.string.default_email))
            .toString()
        return Pair(userId, email)
    }

    fun setSupportActionBarStyle(supportActionBar: ActionBar?) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showError(@StringRes errorString: Int) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
    }

    fun isEmailValid(email: String): Boolean {
        return if (email.isNotBlank()) {
            if (email.contains('@')) {
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            } else {
                false
            }
        } else {
            false
        }
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return (password == passwordConfirm)
    }
}