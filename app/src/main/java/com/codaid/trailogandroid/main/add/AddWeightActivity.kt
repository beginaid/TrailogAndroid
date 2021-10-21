package com.codaid.trailogandroid.main.add

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.Utils
import com.codaid.trailogandroid.common.Utils.Companion.navList
import com.codaid.trailogandroid.common.Utils.Companion.navTitles
import com.codaid.trailogandroid.common.Utils.Companion.optionList
import com.codaid.trailogandroid.common.custom_model.Weight
import com.codaid.trailogandroid.databinding.ActivityAddWeightBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class AddWeightActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityAddWeightBinding
    private var mGenre by Delegates.notNull<Int>()
    private lateinit var userId: String
    private lateinit var email: String
    private var utils = Utils()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWeightBinding.inflate(layoutInflater)
        val date = binding.appBar.contentAddWeight.date
        val weight = binding.appBar.contentAddWeight.weight
        val add = binding.appBar.contentAddWeight.add
        val dateDelete = binding.appBar.contentAddWeight.dateDelete
        val delete = binding.appBar.contentAddWeight.delete
        val loading = binding.appBar.contentAddWeight.loading
        val root = binding.root

        mGenre = 1
        setContentView(root)
        val userIdEmail = utils.setSharedPreference()
        userId = userIdEmail.first
        email = userIdEmail.second
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
            utils.showDatePicker(date)
        }
        dateDelete.setOnClickListener {
            utils.showDatePicker(dateDelete)
        }

        add.setOnClickListener {
            when {
                date.text.toString().isBlank() -> {
                    utils.showError(R.string.invalid_date)
                }
                weight.text.toString().isBlank() -> {
                    utils.showError(R.string.invalid_weight)
                }
                else -> {
                    loading.visibility = View.VISIBLE
                    val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    val weightAdded = Weight(weight.text.toString().toFloat())
                    db.collection("weights_$userId").document(date.text.toString()).set(weightAdded)
                        .addOnSuccessListener {
                            loading.visibility = View.GONE
                            startActivity(Intent(applicationContext, MainActivity()::class.java))
                        }
                        .addOnFailureListener {
                            loading.visibility = View.GONE
                            utils.showError(R.string.failed_add_weight)
                        }
                }
            }
        }

        delete.setOnClickListener {
            if (dateDelete.text.toString().isBlank()) {
                utils.showError(R.string.invalid_date)
            } else {
                loading.visibility = View.VISIBLE
                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                db.collection("weights_$userId").document(dateDelete.text.toString()).delete()
                    .addOnSuccessListener {
                        loading.visibility = View.GONE
                        startActivity(Intent(applicationContext, MainActivity()::class.java))
                    }
                    .addOnFailureListener {
                        loading.visibility = View.GONE
                        utils.showError(R.string.failed_delete_weight)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGenre = 1
        utils.selectItem(binding.navView, mGenre)
        binding.appBar.toolbar.title = navTitles[mGenre]
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        utils.goAnotherActivity(binding.appBar.toolbar, optionList.indexOf(item.itemId), "option")
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        utils.goAnotherActivity(binding.appBar.toolbar, navList.indexOf(item.itemId), "nav")
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}