package com.codaid.trailogandroid.main.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.databinding.ActivitySettingBinding
import com.codaid.trailogandroid.LoginActivity
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.codaid.trailogandroid.main.add_training.AddTrainingActivity
import com.codaid.trailogandroid.main.add_weight.AddWeightActivity
import com.codaid.trailogandroid.main.add_workout.AddWorkoutActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class SettingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var navList: List<Int>
    private lateinit var navTitles: List<String>
    private lateinit var activityList: List<AppCompatActivity>
    private lateinit var optionActivityList: List<AppCompatActivity>
    private lateinit var optionList: List<Int>
    private lateinit var mIntent: Intent
    private lateinit var loginViewModel: LoginViewModel
    private var mGenre by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 4
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavs()
        createToolbar()
        binding.appBar.toolbar.title = navTitles[mGenre]
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        binding.appBar.contentSetting.logout.setOnClickListener {
            loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)
            CoroutineScope(Dispatchers.Main).launch {
                loginViewModel.logout()
                sharedPref.edit().clear().apply()
            }
            val intentLogin = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intentLogin)
        }
    }

    override fun onResume() {
        super.onResume()
        mGenre = 4
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

    private fun goAnotherActivity(mGenre: Int, loc: String) {
        binding.appBar.toolbar.title = navTitles[mGenre]
        if (loc == "nav") {
            mIntent = Intent(applicationContext, activityList[mGenre]::class.java)
        } else if (loc == "option") {
            mIntent = Intent(applicationContext, optionActivityList[mGenre]::class.java)
        }
        startActivity(mIntent)
    }

    private fun createToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val email = sharedPref.getString(getString(R.string.saved_email), getString(R.string.default_email))
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