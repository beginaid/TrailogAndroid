package com.codaid.trailogandroid.main.add_training

data class AddTrainingFormState (
    val dateError: Int? = null,
    val weightError: Int? = null,
    val weightErrorIndex: Int? = null,
    val repsError: Int? = null,
    val repsErrorIndex: Int? = null,
    val isDataValid: Boolean = false
)