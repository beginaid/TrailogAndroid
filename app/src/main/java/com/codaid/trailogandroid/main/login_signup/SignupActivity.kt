package com.codaid.trailogandroid.main.login_signup

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.Utils
import com.codaid.trailogandroid.databinding.ActivitySignupBinding
import com.codaid.trailogandroid.main.dash_board.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        utils.setSupportActionBarStyle(supportActionBar)

        val email = binding.email
        val password = binding.password
        val passwordConfirm = binding.passwordConfirm
        val signup = binding.signup
        val loading = binding.loading

        signup.setOnClickListener {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            if (!utils.isEmailValid(email.text.toString())) {
                utils.showError(R.string.invalid_email)
            } else if (!utils.isPasswordValid(password.text.toString())) {
                utils.showError(R.string.invalid_password)
            } else if (!utils.isPasswordConfirmValid(
                    password.text.toString(),
                    passwordConfirm.text.toString()
                )
            ) {
                utils.showError(R.string.invalid_password_confirm)
            } else {
                loading.visibility = View.VISIBLE
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnSuccessListener {
                        utils.setUserIdEmail(auth.currentUser)
                        utils.clearAndGoActivity(MainActivity(), "main")
                        loading.visibility = View.GONE
                    }
                    .addOnFailureListener { e ->
                        val errorCode = (e as FirebaseAuthException).errorCode
                        if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                            utils.showError(R.string.error_message_already_in_use)
                            loading.visibility = View.GONE
                        } else {
                            utils.showError(R.string.failed_signup)
                            loading.visibility = View.GONE
                        }
                    }
            }
        }
    }
}