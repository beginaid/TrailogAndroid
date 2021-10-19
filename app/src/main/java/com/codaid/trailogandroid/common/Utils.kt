package com.codaid.trailogandroid.common

import android.content.Context
import android.util.JsonToken
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat.getSystemService
import com.codaid.trailogandroid.MyApplication

class Utils {

    private var context = MyApplication.instance

    fun setSupportActionBarStyle(supportActionBar: ActionBar?) {
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun showError(@StringRes errorString: Int) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
    }

    fun isEmailValid(email: String): Boolean {
        return if (email.isNotBlank()) {
            if (email.contains('@')) {
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            } else {
                false
            }
        } else {
            false
        }
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun isPasswordConfirmValid(password: String, passwordConfirm: String): Boolean {
        return (password == passwordConfirm)
    }
}