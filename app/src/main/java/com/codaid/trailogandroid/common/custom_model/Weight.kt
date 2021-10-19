package com.codaid.trailogandroid.common.custom_model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Weight (
    val weight: Float? = null,
    @ServerTimestamp
    val createdAt: Date? = null
)