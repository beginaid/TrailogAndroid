package com.codaid.trailogandroid.main.add

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.codaid.trailogandroid.common.custom_model.Training
import com.codaid.trailogandroid.databinding.ActivityAddTrainingBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates


class AddTrainingActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAddTrainingBinding
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var email: String
    private val eventList = mutableListOf<String>()
    private val weightList = mutableListOf<String>()
    private val repsList = mutableListOf<String>()
    private val utils = Utils()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTrainingBinding.inflate(layoutInflater)
        val date = binding.appBar.contentAddTraining.date
        val addTrainingForms = binding.appBar.contentAddTraining.addTrainingForms
        val fabPlus = binding.appBar.contentAddTraining.fabPlus
        val fabMinus = binding.appBar.contentAddTraining.fabMinus
        val add = binding.appBar.contentAddTraining.add
        val loading = binding.appBar.contentAddTraining.loading
        var forms: List<View>
        val root = binding.root

        mGenre = 2
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
            val component = View.inflate(this, R.layout.add_training_component, null)
            addTrainingForms.addView(component)
        }

        fabMinus.setOnClickListener {
            val childCount = addTrainingForms.childCount
            if (childCount > 1) {
                addTrainingForms.removeViewAt(childCount - 1)
            }
        }

        add.setOnClickListener {
            forms = binding.appBar.contentAddTraining.addTrainingForms.children.toList()
            when {
                !utils.checkTrainingFormsFilled(forms) -> {
                    println("error!!!!")
                    utils.showError(R.string.invalid_form)
                }
                else -> {
                    loading.visibility = View.VISIBLE
                    val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    for (form in forms) {
                        val event = form.findViewById<Spinner>(R.id.event).selectedItem.toString()
                        val weight = form.findViewById<EditText>(R.id.weight).text.toString()
                        val reps = form.findViewById<EditText>(R.id.reps).text.toString()
                        eventList.add(event)
                        weightList.add(weight)
                        repsList.add(reps)
                    }

                    val items = mutableMapOf<String, Map<String, String>>()
                    val docRefAlready =
                        db.collection("trainings_$userId").document(date.text.toString())
                    docRefAlready.get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val item = mutableMapOf<String, String>()

                                @Suppress("UNCHECKED_CAST")
                                val contentsMap =
                                    document.get("contents") as MutableMap<String, MutableMap<String, String>>
                                for (key in contentsMap.keys) {
                                    item["??????"] = contentsMap[key]?.get("??????").toString()
                                    item["??????"] = contentsMap[key]?.get("??????").toString()
                                    items[key] = item
                                }
                            }
                            for (i in eventList.indices) {
                                val item = mutableMapOf<String, String>()
                                item["??????"] = weightList[i]
                                item["??????"] = repsList[i]
                                items[eventList[i]] = item
                            }
                            val trainingAdded = Training(items)

                            val docRef =
                                db.collection("trainings_$userId").document(date.text.toString())
                            if (docRef.get().isSuccessful) {
                                docRef.update("contents", trainingAdded.contents)
                            } else {
                                docRef.set(trainingAdded)
                            }
                            loading.visibility = View.GONE
                            utils.clearAndGoActivity(MainActivity(), "training")
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
        mGenre = 2
        utils.selectItem(binding.navView, mGenre)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("optionMenuItemSelected")
        super.onOptionsItemSelected(item)
        println(item)
        utils.goAnotherActivity(binding.appBar.toolbar, optionList.indexOf(item.itemId), "option")
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        println("navigationMenuItemSelected")
        utils.goAnotherActivity(binding.appBar.toolbar, navList.indexOf(item.itemId), "nav")
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}