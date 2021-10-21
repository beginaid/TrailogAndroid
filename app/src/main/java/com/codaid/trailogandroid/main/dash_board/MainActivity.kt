package com.codaid.trailogandroid.main.dash_board

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.Utils
import com.codaid.trailogandroid.common.Utils.Companion.navList
import com.codaid.trailogandroid.common.Utils.Companion.navTitles
import com.codaid.trailogandroid.common.Utils.Companion.optionList
import com.codaid.trailogandroid.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this) }
    private lateinit var binding: ActivityMainBinding
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var email: String
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 0
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userIdEmail = utils.setSharedPreference()
        userId = userIdEmail.first
        email = userIdEmail.second
        utils.createTabs(binding.appBar.viewPager2, viewPagerAdapter, binding.appBar.tabLayout)
        utils.createToolbar(
            this,
            supportActionBar,
            binding.navView,
            binding.drawerLayout,
            binding.appBar.toolbar
        )
        binding.navView.setNavigationItemSelectedListener(this)
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
        utils.selectItem(binding.navView, mGenre)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        println("drawer click!!")
        utils.goAnotherActivity(binding.appBar.toolbar, optionList.indexOf(item.itemId), "option")
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        println("drawer click!!")
        utils.goAnotherActivity(binding.appBar.toolbar, navList.indexOf(item.itemId), "nav")
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun onClickAddWeight(view: View) {
        utils.goAnotherActivity(binding.appBar.toolbar, 1, "nav")
    }

    fun onClickAddTraining(view: View) {
        utils.goAnotherActivity(binding.appBar.toolbar, 2, "nav")
    }

    fun onClickAddWorkout(view: View) {
        utils.goAnotherActivity(binding.appBar.toolbar, 3, "nav")
    }

}