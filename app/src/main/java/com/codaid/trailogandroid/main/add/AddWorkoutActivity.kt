package com.codaid.trailogandroid.main.add

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.children
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.Utils
import com.codaid.trailogandroid.common.Utils.Companion.navList
import com.codaid.trailogandroid.common.Utils.Companion.navTitles
import com.codaid.trailogandroid.common.Utils.Companion.optionList
import com.codaid.trailogandroid.common.custom_model.Workout
import com.codaid.trailogandroid.databinding.ActivityAddWorkoutBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class AddWorkoutActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAddWorkoutBinding
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var email: String
    private val eventList = mutableListOf<String>()
    private val minutesList = mutableListOf<String>()
    private val maxBpmList = mutableListOf<String>()
    private val avgBpmList = mutableListOf<String>()
    private val utils = Utils()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkoutBinding.inflate(layoutInflater)
        val date = binding.appBar.contentAddWorkout.date
        val addWorkoutForms = binding.appBar.contentAddWorkout.addWorkoutForms
        val fabPlus = binding.appBar.contentAddWorkout.fabPlus
        val fabMinus = binding.appBar.contentAddWorkout.fabMinus
        val add = binding.appBar.contentAddWorkout.add
        val loading = binding.appBar.contentAddWorkout.loading
        var forms: List<View>
        val root = binding.root

        mGenre = 3
        setContentView(root)
        val userIdEmail = utils.setSharedPreference()
        userId = userIdEmail.first
        email = userIdEmail.second
        setSupportActionBar(binding.appBar.toolbar)
        utils.createToolbar(
            this,
            supportActionBar,
            binding.navView,
            binding.drawerLayout,
            binding.appBar.toolbar
        )
        binding.navView.setNavigationItemSelectedListener(this)
        utils.setDefaultDate(date)
        date.setOnClickListener {
            utils.createDatePicker(date, this)
        }

        fabPlus.setOnClickListener {
            val component = View.inflate(this, R.layout.add_workout_component, null)
            addWorkoutForms.addView(component)
        }

        fabMinus.setOnClickListener {
            val childCount = addWorkoutForms.childCount
            if (childCount > 1) {
                addWorkoutForms.removeViewAt(childCount - 1)
            }
        }

        add.setOnClickListener {
            forms = binding.appBar.contentAddWorkout.addWorkoutForms.children.toList()
            when {
                !utils.checkWorkoutFormsFilled(forms) -> {
                    utils.showError(R.string.invalid_form)
                }
                else -> {
                    loading.visibility = View.VISIBLE
                    val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
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
                    val items = mutableMapOf<String, Map<String, String>>()
                    val docRefAlready =
                        db.collection("workouts_$userId").document(date.text.toString())
                    docRefAlready.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val item = mutableMapOf<String, String>()

                                @Suppress("UNCHECKED_CAST")
                                val contentsMap =
                                    document.get("contents") as MutableMap<String, MutableMap<String, String>>
                                for (key in contentsMap.keys) {
                                    item["時間"] = contentsMap[key]?.get("時間").toString()
                                    item["最大心拍"] = contentsMap[key]?.get("最大心拍").toString()
                                    item["平均心拍"] = contentsMap[key]?.get("平均心拍").toString()
                                    items[key] = item
                                }
                            }
                            for (i in eventList.indices) {
                                val item = mutableMapOf<String, String>()
                                item["時間"] = minutesList[i]
                                item["最大心拍"] = maxBpmList[i]
                                item["平均心拍"] = avgBpmList[i]
                                items[eventList[i]] = item
                            }
                            val workoutAdded = Workout(items)

                            val docRef =
                                db.collection("workouts_$userId").document(date.text.toString())
                            if (docRef.get().isSuccessful) {
                                docRef.update("contents", workoutAdded.contents)
                            } else {
                                docRef.set(workoutAdded)
                            }
                            loading.visibility = View.GONE
                            utils.clearAndGoActivity(MainActivity(), "workout")
                        }
                        .addOnFailureListener {
                            loading.visibility = View.GONE
                            utils.showError(R.string.failed_add_weight)
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGenre = 3
        utils.selectItem(binding.navView, mGenre)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        utils.goAnotherActivity(
            binding.appBar.toolbar,
            optionList.indexOf(item.itemId),
            "option"
        )
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        utils.goAnotherActivity(binding.appBar.toolbar, navList.indexOf(item.itemId), "nav")
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}