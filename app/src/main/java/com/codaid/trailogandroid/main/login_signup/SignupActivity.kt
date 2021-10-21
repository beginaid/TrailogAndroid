package com.codaid.trailogandroid.main.login_signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignupBinding
    private val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_signup)
//        setSupportActionBar(binding.toolbar)
//        utils.setSupportActionBarStyle(supportActionBar)

        val email = binding.email
        val password = binding.password
        val passwordConfirm = binding.passwordConfirm
        val signup = binding.signup
        val loading = binding.loading

        signup.setOnClickListener {
            println("signupClicked!!")
        }

//        signup.setOnClickListener {
//            println("signupClicked!!")
//            if (!utils.isEmailValid(email.text.toString())) {
//                println("a")
//                utils.showError(R.string.invalid_email)
//            } else if (!utils.isPasswordValid(password.text.toString())) {
//                println("b")
//                utils.showError(R.string.invalid_password)
//            } else if (!utils.isPasswordConfirmValid(password.text.toString(), passwordConfirm.text.toString())) {
//                println("c")
//                utils.showError(R.string.invalid_password_confirm)
//            } else {
//                println("d")
//                loading.visibility = View.VISIBLE
//                val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                im.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
//                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
//                    .addOnSuccessListener {
//                        utils.clearAndGoActivity(MainActivity())
//                    }
//                    .addOnFailureListener { e ->
//                        val errorCode = (e as FirebaseAuthException).errorCode
//                        if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
//                            utils.showError(R.string.error_message_already_in_use)
//                        } else {
//                            utils.showError(R.string.failed_signup)
//                        }
//                    }
//            }
//        }
    }

//    override fun onSupportNavigateUp(): Boolean {
//        super.onSupportNavigateUp()
//        val intent = Intent(applicationContext, LoginActivity::class.java)
//        startActivity(intent)
//        return true
//    }
}