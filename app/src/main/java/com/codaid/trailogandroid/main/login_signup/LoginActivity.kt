package com.codaid.trailogandroid.main.login_signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.Utils
import com.codaid.trailogandroid.databinding.ActivityLoginBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        binding.register.setOnClickListener {
            println("signup!!")
            startActivity(Intent(applicationContext, SignupActivity::class.java))
        }

        login.setOnClickListener {
            val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            println(email.text.toString())
            if (!utils.isEmailValid(email.text.toString())) {
                utils.showError(R.string.invalid_email)
            } else if (!utils.isPasswordValid(password.text.toString())) {
                utils.showError(R.string.invalid_password)
            } else {
                loading.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnSuccessListener {
                        utils.setUserIdEmail(auth.currentUser)
                        utils.clearAndGoActivity(MainActivity(), "main")
                        loading.visibility = View.GONE
                    }
                    .addOnFailureListener {
                        utils.showError(R.string.failed_login)
                        loading.visibility = View.GONE
                    }
            }
        }
    }
}