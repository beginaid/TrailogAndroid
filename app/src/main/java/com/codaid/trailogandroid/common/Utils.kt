package com.codaid.trailogandroid.common

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.codaid.trailogandroid.MyApplication
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.main.add.AddTrainingActivity
import com.codaid.trailogandroid.main.add.AddWeightActivity
import com.codaid.trailogandroid.main.add.AddWorkoutActivity
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.codaid.trailogandroid.main.dash_board.SettingActivity
import com.codaid.trailogandroid.main.dash_board.ViewPagerAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

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

    fun checkTrainingFormsFilled(forms: List<View>): Boolean {
        var flag = true
        for (form in forms) {
            val thisEvent = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
            val thisWeight = form.findViewById<EditText>(R.id.weight).text.toString()
            val thisReps = form.findViewById<EditText>(R.id.reps).text.toString()
            if (thisEvent.isBlank() || thisWeight.isBlank() || thisReps.isBlank()) {
                flag = false
            }
        }
        return flag
    }

    fun checkWorkoutFormsFilled(forms: List<View>): Boolean {
        var flag = true
        for (form in forms) {
            val thisEvent = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
            val thisMinutes = form.findViewById<EditText>(R.id.minutes).text.toString()
            val thisAvgMpm = form.findViewById<EditText>(R.id.avg_bpm).text.toString()
            val thisMaxMpm = form.findViewById<EditText>(R.id.max_bpm).text.toString()
            if (thisEvent.isBlank() || thisMinutes.isBlank() || thisAvgMpm.isBlank() || thisMaxMpm.isBlank()) {
                flag = false
            }
        }
        return flag
    }

    fun createDatePicker(editText: EditText, activity: Activity) {
        val localDate = LocalDate.now()
        val datePickerDialog = context?.let {
            DatePickerDialog(
                activity,
                R.style.DialogTheme,
                { _, year, month, dayOfMonth ->
                    editText.setText(
                        context?.getString(
                            R.string.date_format,
                            year,
                            month + 1,
                            dayOfMonth
                        )
                    )
                },
                localDate.year,
                localDate.monthValue - 1,
                localDate.dayOfMonth
            )
        }
        datePickerDialog?.show()
        val positiveColor = ContextCompat.getColor(context!!, R.color.primary)
        val negativeColor = ContextCompat.getColor(context!!, R.color.accent)
        datePickerDialog?.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(positiveColor)
        datePickerDialog?.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(negativeColor)
    }

    fun setDefaultDate(date: EditText) {
        val localDate = LocalDate.now()
        date.setText(
            context?.getString(
                R.string.date_format,
                localDate.year,
                localDate.monthValue,
                localDate.dayOfMonth
            )
        )
    }

    fun goAnotherActivity(toolbar: androidx.appcompat.widget.Toolbar, mGenre: Int, loc: String) {
//        toolbar.title = navTitles[mGenre]
        println(mGenre)
        if (loc == "nav") {
            mIntent = Intent(context, activityList[mGenre]::class.java)
        } else if (loc == "option") {
            mIntent = Intent(context, optionActivityList[mGenre]::class.java)
        }
        mIntent.flags = FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(mIntent)
    }

    fun clearAndGoActivity(activity: AppCompatActivity, extra: String) {
        val intent = Intent(context, activity::class.java)
        intent.flags = FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("tab", extra)
        context?.startActivity(intent)
    }

    fun createToolbar(
        activity: Activity,
        supportActionBar: ActionBar?,
        navView: NavigationView,
        drawerLayout: DrawerLayout,
        toolbar: androidx.appcompat.widget.Toolbar
    ) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val sharedPref = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val email = sharedPref?.getString(
            context?.getString(R.string.saved_email),
            context?.getString(R.string.default_email)
        )
        navView.getHeaderView(0).findViewById<TextView>(R.id.header_email).text = email
        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar,
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

    fun createTabs(
        viewPager2: ViewPager2,
        viewPagerAdapter: ViewPagerAdapter,
        tabLayout: TabLayout
    ) {
        viewPager2.apply {
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = viewPagerAdapter.itemCount
        }
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    fun setUserIdEmail(model: FirebaseUser?) {
        val sharedPref = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        with(sharedPref?.edit()) {
            this?.putString(context?.getString(R.string.saved_user_id), model?.uid)
            this?.putString(context?.getString(R.string.saved_email), model?.email)
            this?.commit()
        }
    }

    fun setSharedPreference(): Pair<String, String> {
        val sharedPref = context?.getSharedPreferences(
            context?.getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        val userId = sharedPref?.getString(
            context?.getString(R.string.saved_user_id),
            context?.getString(R.string.default_user_id)
        )
            .toString()
        val email = sharedPref?.getString(
            context?.getString(R.string.saved_email),
            context?.getString(R.string.default_email)
        )
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