package com.codaid.trailogandroid.main.add_workout

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.children
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.databinding.ActivityAddWorkoutBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.codaid.trailogandroid.main.setting.SettingActivity
import com.codaid.trailogandroid.main.add_training.AddTrainingActivity
import com.codaid.trailogandroid.main.add_weight.AddWeightActivity
import com.codaid.trailogandroid.main.add_weight.afterTextChanged
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.properties.Delegates

class AddWorkoutActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityAddWorkoutBinding
    private lateinit var navList: List<Int>
    private lateinit var navTitles: List<String>
    private lateinit var activityList: List<AppCompatActivity>
    private lateinit var optionActivityList: List<AppCompatActivity>
    private lateinit var optionList: List<Int>
    private lateinit var mIntent: Intent
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var addWorkoutViewModel: AddWorkoutViewModel
    private lateinit var userId: String
    private val eventList = mutableListOf<String>()
    private val minutesList = mutableListOf<String>()
    private val maxBpmList = mutableListOf<String>()
    private val avgBpmList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 3
        binding = ActivityAddWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSharedPreference()
        setNavs()
        createToolbar()
        setDefaultDate()

        val date = binding.appBar.contentAddWorkout.date
        val addWorkoutForms = binding.appBar.contentAddWorkout.addWorkoutForms
        val fabPlus = binding.appBar.contentAddWorkout.fabPlus
        val fabMinus = binding.appBar.contentAddWorkout.fabMinus
        val add = binding.appBar.contentAddWorkout.add
        val loading = binding.appBar.contentAddWorkout.loading
        var forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
        applyAfterTextChangedToAllForms(forms, date)

        date.setOnClickListener {
            showDatePicker(date)
        }

        fabPlus.setOnClickListener {
            val component = View.inflate(this, R.layout.add_workout_component, null)
            addWorkoutForms.addView(component)
            forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
            applyAfterTextChangedToAllForms(forms, date)
            add.isEnabled = false
        }

        fabMinus.setOnClickListener {
            val childCount = addWorkoutForms.childCount
            if (childCount > 1){
                addWorkoutForms.removeViewAt(childCount - 1)
            }
        }

        addWorkoutViewModel = ViewModelProvider(this, AddWorkoutViewModelFactory())
            .get(AddWorkoutViewModel::class.java)

        addWorkoutViewModel.addWorkoutFormState.observe(this@AddWorkoutActivity, Observer {
            val addWorkoutState = it ?: return@Observer

            add.isEnabled = addWorkoutState.isDataValid && checkFormsFilled()

            forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
            if (addWorkoutState.dateError != null) {
                date.error = getString(addWorkoutState.dateError)
            }
            if (addWorkoutState.minutesError != null) {
                forms[addWorkoutState.minutesErrorIndex!!].findViewById<EditText>(R.id.minutes).error = getString(addWorkoutState.minutesError)
            }
            if (addWorkoutState.maxBpmError != null) {
                forms[addWorkoutState.maxBpmErrorIndex!!].findViewById<EditText>(R.id.max_bpm).error = getString(addWorkoutState.maxBpmError)
            }
            if (addWorkoutState.avgBpmError != null) {
                forms[addWorkoutState.avgBpmErrorIndex!!].findViewById<EditText>(R.id.avg_bpm).error = getString(addWorkoutState.avgBpmError)
            }
        })

        addWorkoutViewModel.workoutResult.observe(this@AddWorkoutActivity, Observer {
            val addWorkoutResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (addWorkoutResult.error != null) {
                showAddWorkoutFailed(addWorkoutResult.error)
            }
            if (addWorkoutResult.success != null) {
                updateUiWithUser()
            }
            setResult(Activity.RESULT_OK)
        })

        add.setOnClickListener {
            loading.visibility = View.VISIBLE
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
            for (form in forms) {
                val event = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
                val minutes = form.findViewById<EditText>(R.id.minutes).text.toString()
                val maxBpm = form.findViewById<EditText>(R.id.max_bpm).text.toString()
                val avgBpm = form.findViewById<EditText>(R.id.avg_bpm).text.toString()
                eventList.add(event)
                minutesList.add(minutes)
                maxBpmList.add(maxBpm)
                avgBpmList.add(avgBpm)
            }
            CoroutineScope(Dispatchers.Main).launch {
                addWorkoutViewModel.addWorkout(userId, date.text.toString(), eventList, minutesList, maxBpmList, avgBpmList)
            }
        }
    }

    private fun reCheck(date: String, elem: View, i: Int) {
        val thisMinutes = elem.findViewById<EditText>(R.id.minutes).text.toString()
        val thisMaxBpm = elem.findViewById<EditText>(R.id.max_bpm).text.toString()
        val thisAvgBpm = elem.findViewById<EditText>(R.id.avg_bpm).text.toString()
        addWorkoutViewModel.addDataChanged(date, thisMinutes, i, thisMaxBpm, i, thisAvgBpm, i)
    }

    private fun applyAfterTextChangedToAllForms(forms: List<View>, date: EditText) {
        for (i in forms.indices) {
            forms[i].findViewById<EditText>(R.id.minutes).afterTextChanged {
                reCheck(date.text.toString(), forms[i], i)
            }
            forms[i].findViewById<EditText>(R.id.max_bpm).afterTextChanged {
                reCheck(date.text.toString(), forms[i], i)
            }
            forms[i].findViewById<EditText>(R.id.avg_bpm).afterTextChanged {
                reCheck(date.text.toString(), forms[i], i)
            }
        }
    }

    private fun checkFormsFilled(): Boolean {
        val thisEventList = mutableListOf<String>()
        val thisMinutesList = mutableListOf<String>()
        val thisMaxBpmList = mutableListOf<String>()
        val thisAvgBpmList = mutableListOf<String>()
        val forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
        for (form in forms) {
            val thisEvent = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
            val thisMinutes = form.findViewById<EditText>(R.id.minutes).text.toString()
            val thisMaxBpm = form.findViewById<EditText>(R.id.max_bpm).text.toString()
            val thisAvgBpm = form.findViewById<EditText>(R.id.avg_bpm).text.toString()
            if (thisEvent.isNotBlank()) {
                thisEventList.add(thisEvent)
            }
            if (thisMinutes.isNotBlank()) {
                thisMinutesList.add(thisMinutes)
            }
            if (thisMaxBpm.isNotBlank()) {
                thisMaxBpmList.add(thisMaxBpm)
            }
            if (thisAvgBpm.isNotBlank()) {
                thisAvgBpmList.add(thisAvgBpm)
            }
        }
        return (thisEventList.size == forms.size && thisMinutesList.size == forms.size && thisMaxBpmList.size == forms.size && thisAvgBpmList.size == forms.size)
    }

    private fun showAddWorkoutFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun updateUiWithUser() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("tab", "workout")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mGenre = 3
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

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
    }

    private fun setSharedPreference() {
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )
        userId = sharedPref.getString(getString(R.string.saved_user_id), getString(R.string.default_user_id))
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

    private fun createToolbar() {
        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val email = sharedPref.getString(getString(R.string.saved_user_id), getString(R.string.default_email))
        binding.navView.getHeaderView(0).findViewById<TextView>(R.id.header_email).text = email
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.appBar.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    private fun selectItem(index: Int) {
        val navigationView = binding.navView.menu
        navigationView.getItem(index).isChecked = true
    }

    private fun setDefaultDate() {
        val localDate = LocalDate.now()
        binding.appBar.contentAddWorkout.date.setText(getString(R.string.date_format, localDate.year, localDate.monthValue, localDate.dayOfMonth))
    }

    private fun showDatePicker(editText: EditText) {
        val localDate = LocalDate.now()
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DialogTheme,
            { _, year, month, dayOfMonth->
                editText.setText(getString(R.string.date_format, year, month + 1, dayOfMonth))
            },
            localDate.year,
            localDate.monthValue - 1,
            localDate.dayOfMonth)
        datePickerDialog.show()
        val positiveColor = ContextCompat.getColor(this, R.color.blue)
        val negativeColor = ContextCompat.getColor(this, R.color.red)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(negativeColor)
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