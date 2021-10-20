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

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private val utils = Utils()

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity()::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        val login = binding.login
        val loading = binding.loading

        binding.register.setOnClickListener {
            startActivity(Intent(applicationContext, SignupActivity::class.java))
        }

        login.setOnClickListener {
            if (!utils.isEmailValid(email)) {
                utils.showError(R.string.invalid_email)
            } else if (!utils.isPasswordValid(password)) {
                utils.showError(R.string.invalid_password)
            } else {
                loading.visibility = View.VISIBLE
                val im = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(applicationContext, MainActivity()::class.java))
                        } else {
                            utils.showError(R.string.failed_login)
                        }
                    }
                loading.visibility = View.GONE
            }
        }
    }
}