package com.codaid.trailogandroid.main.dash_board

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.databinding.ActivityMainBinding
import com.codaid.trailogandroid.main.add_training.AddTrainingActivity
import com.codaid.trailogandroid.main.add_weight.AddWeightActivity
import com.codaid.trailogandroid.main.add_workout.AddWorkoutActivity
import com.codaid.trailogandroid.main.setting.SettingActivity
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }
    private lateinit var binding: ActivityMainBinding
    private lateinit var navList: List<Int>
    private lateinit var navTitles: List<String>
    private lateinit var activityList: List<AppCompatActivity>
    private lateinit var optionActivityList: List<AppCompatActivity>
    private lateinit var optionList: List<Int>
    private lateinit var mIntent: Intent
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 0
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSharedPreference()
        setNavs()
        createTabs()
        createToolbar()
        binding.appBar.toolbar.title = navTitles[mGenre]
        if (intent.getStringExtra("tab") == "training") {
            binding.appBar.viewPager2.setCurrentItem(1, false)
        }
        if (intent.getStringExtra("tab") == "workout") {
            binding.appBar.viewPager2.setCurrentItem(2, false)
        }

    }

    override fun onResume() {
        super.onResume()
        mGenre = 0
        selectItem(mGenre)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        goAnotherActivity(optionList.indexOf(item.itemId), "option")
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        goAnotherActivity(navList.indexOf(item.itemId), "nav")
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onClickAddWeight(view: View) {
        goAnotherActivity(1, "nav")
    }

    fun onClickAddTraining(view: View) {
        goAnotherActivity(2, "nav")
    }

    fun onClickAddWorkout(view: View) {
        goAnotherActivity(3, "nav")
    }

    private fun setSharedPreference() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        userId = sharedPref.getString(getString(R.string.saved_user_id), getString(R.string.default_user_id))
            .toString()
        email = sharedPref.getString(getString(R.string.saved_email), getString(R.string.default_email))
            .toString()
    }

    private fun goAnotherActivity(mGenre: Int, loc: String) {
        binding.appBar.toolbar.title = navTitles[mGenre]
        if (loc == "nav") {
            mIntent = Intent(applicationContext, activityList[mGenre]::class.java)
        } else if (loc == "option") {
            mIntent = Intent(applicationContext, optionActivityList[mGenre]::class.java)
        }
        startActivity(mIntent)
    }

    private fun createTabs() {
        binding.appBar.viewPager2.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = viewPagerAdapter.itemCount
        }
        TabLayoutMediator(binding.appBar.tabLayout, binding.appBar.viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    private fun createToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.header_email).text = email
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBar.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
    }

    private fun selectItem(index: Int) {
        val navigationView = binding.navView.menu
        navigationView.getItem(index).isChecked = true
    }

    private fun setNavs() {
        navList = listOf(
            R.id.nav_dash_board,
            R.id.nav_add_weight,
            R.id.nav_add_training,
            R.id.nav_add_workout,
            R.id.nav_settings
        )
        navTitles = listOf(
            getString(R.string.menu_dash_board),
            getString(R.string.menu_add_weight),
            getString(R.string.menu_add_training),
            getString(R.string.menu_add_workout),
            getString(R.string.menu_settings)
        )
        activityList = listOf(
            MainActivity(),
            AddWeightActivity(),
            AddTrainingActivity(),
            AddWorkoutActivity(),
            SettingActivity()
        )
        optionList = listOf(
            R.id.action_settings
        )
        optionActivityList = listOf(
            SettingActivity()
        )
    }
}