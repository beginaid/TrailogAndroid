package com.codaid.trailogandroid.main.add_training

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.ResultTraining

class AddTrainingViewModel(private val trainingDataSource: TrainingDataSource) : ViewModel() {

    private val _addTrainingForm = MutableLiveData<AddTrainingFormState>()
    val addWeightFormState: LiveData<AddTrainingFormState> = _addTrainingForm

    private val _trainingResult = MutableLiveData<TrainingResult>()
    val trainingResult: LiveData<TrainingResult> = _trainingResult

    fun addTraining(userId: String?, date: String, event: MutableList<String>, weight: MutableList<String>, reps: MutableList<String>) {
        val result = trainingDataSource.addTraining(userId, date, event, weight, reps)
        if (result is ResultTraining.Success) {
            _trainingResult.value =
                TrainingResult(result.data)
        } else {
            _trainingResult.value = TrainingResult(error = R.string.add_weight_failed)
        }
    }

    fun addDataChanged(date: String, weight: String, weightIndex: Int, reps: String, repsIndex: Int) {
        when {
            date.isBlank() -> {
                _addTrainingForm.value = AddTrainingFormState(dateError = R.string.invalid_date)
            }
            weight.isBlank() -> {
                _addTrainingForm.value = AddTrainingFormState(weightError = R.string.invalid_weight_training, weightErrorIndex = weightIndex)
            }
            reps.isBlank() -> {
                _addTrainingForm.value = AddTrainingFormState(repsError = R.string.invalid_reps, repsErrorIndex = repsIndex)
            }
            else -> {
                _addTrainingForm.value = AddTrainingFormState(isDataValid = true)
            }
        }
    }

}