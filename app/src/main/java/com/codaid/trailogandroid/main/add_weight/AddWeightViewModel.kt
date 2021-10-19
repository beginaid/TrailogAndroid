package com.codaid.trailogandroid.main.add_weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.ResultWeight

class AddWeightViewModel(private val weightDataSource: WeightDataSource) : ViewModel() {

    private val _addWeightForm = MutableLiveData<AddWeightFormState>()
    private val _deleteWeightForm = MutableLiveData<DeleteWeightFormState>()
    val addWeightFormState: LiveData<AddWeightFormState> = _addWeightForm
    val deleteWeightFormState: LiveData<DeleteWeightFormState> = _deleteWeightForm

    private val _weightResult = MutableLiveData<WeightResult>()
    val weightResult: LiveData<WeightResult> = _weightResult

    fun addWeight(userId: String?, date: String, weight: Float) {
        val result = weightDataSource.addWeight(userId, date, weight)

        if (result is ResultWeight.Success) {
            _weightResult.value =
                WeightResult(result.data)
        } else {
            _weightResult.value = WeightResult(error = R.string.add_weight_failed)
        }
    }

    fun addDataChanged(date: String, weight: String) {
        when {
            date.isBlank() -> {
                _addWeightForm.value = AddWeightFormState(dateError = R.string.invalid_date)
            }
            weight.isBlank() -> {
                _addWeightForm.value = AddWeightFormState(weightError = R.string.invalid_weight)
            }
            else -> {
                _addWeightForm.value = AddWeightFormState(isDataValid = true)
            }
        }
    }

    fun deleteWeight(userId: String?, date: String) {
        val result = weightDataSource.deleteWeight(userId, date)

        if (result is ResultWeight.Success) {
            _weightResult.value =
                WeightResult(result.data)
        } else {
            _weightResult.value = WeightResult(error = R.string.delete_weight_failed)
        }
    }

    fun deleteDataChanged(date: String) {
        when {
            date.isBlank() -> {
                _deleteWeightForm.value = DeleteWeightFormState(dateError = R.string.invalid_date)
            }
            else -> {
                _deleteWeightForm.value = DeleteWeightFormState(isDataValid = true)
            }
        }
    }
}