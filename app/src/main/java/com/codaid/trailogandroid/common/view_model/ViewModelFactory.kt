package com.codaid.trailogandroid.common.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModel::class.java)) {
            return ViewModel(
                dataSource = DataSource()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}