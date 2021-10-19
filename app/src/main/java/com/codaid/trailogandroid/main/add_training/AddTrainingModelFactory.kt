package com.codaid.trailogandroid.main.add_training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddTrainingViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTrainingViewModel::class.java)) {
            return AddTrainingViewModel(
                trainingDataSource = TrainingDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}