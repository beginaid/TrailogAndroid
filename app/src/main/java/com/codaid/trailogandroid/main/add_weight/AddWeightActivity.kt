package com.codaid.trailogandroid.main.add_weight

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.databinding.ActivityAddWeightBinding
import com.codaid.trailogandroid.main.add_training.AddTrainingActivity
import com.codaid.trailogandroid.main.add_workout.AddWorkoutActivity
import com.codaid.trailogandroid.MainActivity
import com.codaid.trailogandroid.SettingActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.properties.Delegates

class AddWeightActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAddWeightBinding
    private lateinit var navList: List<Int>
    private lateinit var navTitles: List<String>
    private lateinit var activityList: List<AppCompatActivity>
    private lateinit var optionActivityList: List<AppCompatActivity>
    private lateinit var optionList: List<Int>
    private lateinit var mIntent: Intent
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var addWeightViewModel: AddWeightViewModel
    private lateinit var deleteWeightViewModel: AddWeightViewModel
    private lateinit var userId: String
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGenre = 1
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSharedPreference()
        setNavs()
        createToolbar()
        setDefaultDate()

        val date = binding.appBar.contentAddWeight.date
        val weight = binding.appBar.contentAddWeight.weight
        val add = binding.appBar.contentAddWeight.add
        val dateDelete = binding.appBar.contentAddWeight.dateDelete
        val delete = binding.appBar.contentAddWeight.delete
        val loading = binding.appBar.contentAddWeight.loading

        date.setOnClickListener {
            showDatePicker(date)
        }
        dateDelete.setOnClickListener {
            showDatePicker(dateDelete)
        }

        addWeightViewModel = ViewModelProvider(this, AddWeightViewModelFactory())
            .get(AddWeightViewModel::class.java)

        addWeightViewModel.addWeightFormState.observe(this@AddWeightActivity, Observer {
            val addWeightState = it ?: return@Observer

            binding.appBar.contentAddWeight.add.isEnabled = addWeightState.isDataValid

            if (addWeightState.dateError != null) {
                binding.appBar.contentAddWeight.date.error = getString(addWeightState.dateError)
            }
            if (addWeightState.weightError != null) {
                binding.appBar.contentAddWeight.weight.error = getString(addWeightState.weightError)
            }
        })

        addWeightViewModel.weightResult.observe(this@AddWeightActivity, Observer {
            val addWeightResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (addWeightResult.error != null) {
                showAddWeightFailed(addWeightResult.error)
            }
            if (addWeightResult.success != null) {
                updateUiWithUser()
            }
            setResult(Activity.RESULT_OK)
        })

        deleteWeightViewModel = ViewModelProvider(this, AddWeightViewModelFactory())
            .get(AddWeightViewModel::class.java)

        deleteWeightViewModel.deleteWeightFormState.observe(this@AddWeightActivity, Observer {
            val deleteWeightState = it ?: return@Observer

            binding.appBar.contentAddWeight.delete.isEnabled = deleteWeightState.isDataValid

            if (deleteWeightState.dateError != null) {
                binding.appBar.contentAddWeight.date.error = getString(deleteWeightState.dateError)
            }
        })

        deleteWeightViewModel.weightResult.observe(this@AddWeightActivity, Observer {
            val deleteWeightResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (deleteWeightResult.error != null) {
                showAddWeightFailed(deleteWeightResult.error)
            }
            if (deleteWeightResult.success != null) {
                updateUiWithUser()
            }
            setResult(Activity.RESULT_OK)
        })

        date.afterTextChanged {
            addWeightViewModel.addDataChanged(date.text.toString(), weight.text.toString())
        }
        weight.afterTextChanged {
            addWeightViewModel.addDataChanged(date.text.toString(), weight.text.toString())
        }
        dateDelete.afterTextChanged {
            addWeightViewModel.deleteDataChanged(dateDelete.text.toString())
        }
        add.setOnClickListener {
            loading.visibility = View.VISIBLE
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            CoroutineScope(Dispatchers.Main).launch {
                addWeightViewModel.addWeight(userId, date.text.toString(), weight.text.toString().toFloat())
            }
            goAnotherActivity(0, "nav")
        }
        delete.setOnClickListener {
            loading.visibility = View.VISIBLE
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            CoroutineScope(Dispatchers.Main).launch {
                addWeightViewModel.deleteWeight(userId, dateDelete.text.toString())
            }
        }
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

    private fun showAddWeightFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    private fun updateUiWithUser() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        mGenre = 1
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
        binding.appBar.contentAddWeight.date.setText(getString(R.string.date_format, localDate.year, localDate.monthValue, localDate.dayOfMonth))
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

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}