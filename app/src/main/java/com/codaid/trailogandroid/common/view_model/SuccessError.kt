package com.codaid.trailogandroid.common.view_model

import com.google.firebase.auth.FirebaseUser

data class SuccessError (
    val success: FirebaseUser? = null,
    val error: Int? = null
)