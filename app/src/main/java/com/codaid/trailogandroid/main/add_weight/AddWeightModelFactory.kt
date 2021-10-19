package com.codaid.trailogandroid.main.add_weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddWeightViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddWeightViewModel::class.java)) {
            return AddWeightViewModel(
                weightDataSource = WeightDataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}