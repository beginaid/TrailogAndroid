package com.codaid.trailogandroid.main.add_workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddWorkoutViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWorkoutViewModel::class.java)) {
            return AddWorkoutViewModel(
                workoutDataSource = WorkoutDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}