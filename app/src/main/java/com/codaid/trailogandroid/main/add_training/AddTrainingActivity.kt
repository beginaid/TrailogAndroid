package com.codaid.trailogandroid.main.add_training

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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codaid.trailogandroid.databinding.ActivityAddTrainingBinding
import com.codaid.trailogandroid.main.add_workout.AddWorkoutActivity
import com.codaid.trailogandroid.MainActivity
import com.codaid.trailogandroid.SettingActivity
import com.codaid.trailogandroid.main.add_weight.AddWeightActivity
import com.google.android.material.navigation.NavigationView
import kotlin.properties.Delegates
import com.codaid.trailogandroid.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class AddTrainingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityAddTrainingBinding
    private lateinit var navList: List<Int>
    private lateinit var navTitles: List<String>
    private lateinit var activityList: List<AppCompatActivity>
    private lateinit var optionActivityList: List<AppCompatActivity>
    private lateinit var optionList: List<Int>
    private lateinit var mIntent: Intent
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var addTrainingViewModel: AddTrainingViewModel
    private lateinit var userId: String
    private val eventList = mutableListOf<String>()
    private val weightList = mutableListOf<String>()
    private val repsList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 2
        binding = ActivityAddTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSharedPreference()
        setNavs()
        createToolbar()
        setDefaultDate()

        val date = binding.appBar.contentAddTraining.date
        val addTrainingForms = binding.appBar.contentAddTraining.addTrainingForms
        val fabPlus = binding.appBar.contentAddTraining.fabPlus
        val fabMinus = binding.appBar.contentAddTraining.fabMinus
        val add = binding.appBar.contentAddTraining.add
        val loading = binding.appBar.contentAddTraining.loading
        var forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()

        for (i in forms.indices) {
            forms[i].findViewById<EditText>(R.id.weight).afterTextChanged {
                val thisWeight = forms[i].findViewById<EditText>(R.id.weight).text.toString()
                val thisReps = forms[i].findViewById<EditText>(R.id.reps).text.toString()
                addTrainingViewModel.addDataChanged(date.text.toString(), thisWeight, i, thisReps, i)
            }
            forms[i].findViewById<EditText>(R.id.reps).afterTextChanged {
                val thisWeight = forms[i].findViewById<EditText>(R.id.weight).text.toString()
                val thisReps = forms[i].findViewById<EditText>(R.id.reps).text.toString()
                addTrainingViewModel.addDataChanged(date.text.toString(), thisWeight, i, thisReps, i)
            }
        }

        date.setOnClickListener {
            showDatePicker(date)
        }

        fabPlus.setOnClickListener {
            val component = View.inflate(this, R.layout.add_training_component, null)
            addTrainingForms.addView(component)
            forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()
            for (i in forms.indices) {
                forms[i].findViewById<EditText>(R.id.weight).afterTextChanged {
                    val thisWeight = forms[i].findViewById<EditText>(R.id.weight).text.toString()
                    val thisReps = forms[i].findViewById<EditText>(R.id.reps).text.toString()
                    addTrainingViewModel.addDataChanged(date.text.toString(), thisWeight, i, thisReps, i)
                }
                forms[i].findViewById<EditText>(R.id.reps).afterTextChanged {
                    val thisWeight = forms[i].findViewById<EditText>(R.id.weight).text.toString()
                    val thisReps = forms[i].findViewById<EditText>(R.id.reps).text.toString()
                    addTrainingViewModel.addDataChanged(date.text.toString(), thisWeight, i, thisReps, i)
                }
            }
            add.isEnabled = false
        }

        fabMinus.setOnClickListener {
            val childCount = addTrainingForms.childCount
            if (childCount > 1){
                addTrainingForms.removeViewAt(childCount - 1)
            }
        }

        addTrainingViewModel = ViewModelProvider(this, AddTrainingViewModelFactory())
            .get(AddTrainingViewModel::class.java)

        addTrainingViewModel.addWeightFormState.observe(this@AddTrainingActivity, Observer {
            val addTrainingState = it ?: return@Observer

            add.isEnabled = addTrainingState.isDataValid && checkFormsFilled()

            forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()
            if (addTrainingState.dateError != null) {
                date.error = getString(addTrainingState.dateError)
            }
            if (addTrainingState.weightError != null) {
                forms[addTrainingState.weightErrorIndex!!].findViewById<EditText>(R.id.weight).error = getString(addTrainingState.weightError)
            }
            if (addTrainingState.repsError != null) {
                forms[addTrainingState.repsErrorIndex!!].findViewById<EditText>(R.id.reps).error = getString(addTrainingState.repsError)
            }
        })

        addTrainingViewModel.trainingResult.observe(this@AddTrainingActivity, Observer {
            val addTrainingResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (addTrainingResult.error != null) {
                showAddTrainingFailed(addTrainingResult.error)
            }
            if (addTrainingResult.success != null) {
                updateUiWithUser()
            }
            setResult(Activity.RESULT_OK)
        })

        add.setOnClickListener {
            loading.visibility = View.VISIBLE
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()
            for (form in forms) {
                val event = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
                val weight = form.findViewById<EditText>(R.id.weight).text.toString()
                val reps = form.findViewById<EditText>(R.id.reps).text.toString()
                eventList.add(event)
                weightList.add(weight)
                repsList.add(reps)
            }
            CoroutineScope(Dispatchers.Main).launch {
                addTrainingViewModel.addTraining(userId, date.text.toString(), eventList, weightList, repsList)
            }
        }
    }

    private fun checkFormsFilled(): Boolean {
        val thisEventList = mutableListOf<String>()
        val thisWeightList = mutableListOf<String>()
        val thisRepsList = mutableListOf<String>()
        val forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()
        for (form in forms) {
            val thisEvent = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
            val thisWeight = form.findViewById<EditText>(R.id.weight).text.toString()
            val thisReps = form.findViewById<EditText>(R.id.reps).text.toString()
            if (thisEvent.isNotBlank()) {
                thisEventList.add(thisEvent)
            }
            if (thisWeight.isNotBlank()) {
                thisWeightList.add(thisWeight)
            }
            if (thisReps.isNotBlank()) {
                thisRepsList.add(thisReps)
            }
        }
        return (thisEventList.size == forms.size && thisWeightList.size == forms.size && thisRepsList.size == forms.size)
    }

    private fun showAddTrainingFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun updateUiWithUser() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("tab", "training")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mGenre = 2
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
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    private fun selectItem(index: Int) {
        val navigationView = binding.navView.menu
        navigationView.getItem(index).isChecked = true
    }

    private fun setDefaultDate() {
        val localDate = LocalDate.now()
        binding.appBar.contentAddTraining.date.setText(getString(R.string.date_format, localDate.year, localDate.monthValue, localDate.dayOfMonth))
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