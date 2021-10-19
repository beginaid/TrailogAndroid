package com.codaid.trailogandroid.main.add_workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codaid.trailogandroid.R
import com.codaid.trailogandroid.common.ResultWorkout

class AddWorkoutViewModel(private val workoutDataSource: WorkoutDataSource) : ViewModel() {

    private val _addWorkoutForm = MutableLiveData<AddWorkoutFormState>()
    val addWorkoutFormState: LiveData<AddWorkoutFormState> = _addWorkoutForm

    private val _trainingResult = MutableLiveData<WorkoutResult>()
    val workoutResult: LiveData<WorkoutResult> = _trainingResult

    fun addWorkout(userId: String?, date: String, eventList: MutableList<String>, minutesList: MutableList<String>, maxBpmList: MutableList<String>, avgBpmList: MutableList<String>) {
        val result = workoutDataSource.addWorkout(userId, date, eventList, minutesList, maxBpmList, avgBpmList)
        if (result is ResultWorkout.Success) {
            _trainingResult.value =
                WorkoutResult(result.data)
        } else {
            _trainingResult.value = WorkoutResult(error = R.string.add_weight_failed)
        }
    }

    fun addDataChanged(date: String, minutes: String, minutesIndex: Int, maxBpm: String, maxBpmIndex: Int, avgBpm: String, avgBpmIndex: Int) {
        when {
            date.isBlank() -> {
                _addWorkoutForm.value = AddWorkoutFormState(dateError = R.string.invalid_date)
            }
            minutes.isBlank() -> {
                _addWorkoutForm.value = AddWorkoutFormState(minutesError = R.string.invalid_minutes, minutesErrorIndex = minutesIndex)
            }
            maxBpm.isBlank() -> {
                _addWorkoutForm.value = AddWorkoutFormState(maxBpmError = R.string.invalid_max_bpm, maxBpmErrorIndex = maxBpmIndex)
            }
            avgBpm.isBlank() -> {
                _addWorkoutForm.value = AddWorkoutFormState(avgBpmError = R.string.invalid_avg_bpm, avgBpmErrorIndex = avgBpmIndex)
            }
            else -> {
                _addWorkoutForm.value = AddWorkoutFormState(isDataValid = true)
            }
        }
    }
}