package com.codaid.trailogandroid.common.view_model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DataSource {
    private lateinit var mAuth: FirebaseAuth

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        mAuth = Firebase.auth
        return try{
            val result = mAuth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user!!)
        } catch (e: Exception) {
            Result.Error((e as FirebaseAuthException).errorCode)
        }
    }

    fun logout()  {
        Firebase.auth.signOut()
    }
}