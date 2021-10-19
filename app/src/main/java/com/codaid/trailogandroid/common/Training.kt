package com.codaid.trailogandroid.common

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Training (
    val contents: MutableMap<String, Map<String, String>>? = null,
    @ServerTimestamp
    val createdAt: Date? = null
)