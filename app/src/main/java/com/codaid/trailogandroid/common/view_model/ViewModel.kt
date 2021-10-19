package com.codaid.trailogandroid.common.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.codaid.trailogandroid.R

class ViewModel(private val dataSource: DataSource) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _loginResult = MutableLiveData<SuccessError>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginResult: LiveData<SuccessError> = _loginResult

    suspend fun login(email: String, password: String) {
        val result = dataSource.login(email, password)
        if (result is Result.Success) {
            _loginResult.postValue(SuccessError(result.data))
        } else {
            _loginResult.postValue(SuccessError(error = R.string.failed_login))
        }
    }

    fun logout() {
        dataSource.logout()
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return if (email.isNotBlank()) {
            if (email.contains('@')) {
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
            } else{
                false
            }
        } else {
            false
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}