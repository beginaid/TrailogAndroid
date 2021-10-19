package com.codaid.trailogandroid.main.add_workout

data class AddWorkoutFormState (
    val dateError: Int? = null,
    val minutesError: Int? = null,
    val minutesErrorIndex: Int? = null,
    val maxBpmError: Int? = null,
    val maxBpmErrorIndex: Int? = null,
    val avgBpmError: Int? = null,
    val avgBpmErrorIndex: Int? = null,
    val isDataValid: Boolean = false
)