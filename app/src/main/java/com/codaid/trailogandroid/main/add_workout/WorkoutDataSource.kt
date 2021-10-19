package com.codaid.trailogandroid.main.add_workout

import android.util.Log
import com.codaid.trailogandroid.common.*
import com.codaid.trailogandroid.common.custom_model.Workout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WorkoutDataSource {
    private val db = Firebase.firestore

    fun addWorkout(userId: String?, date: String, eventList: MutableList<String>, minutesList: MutableList<String>, maxBpmList: MutableList<String>, avgBpmList: MutableList<String>): ResultWorkout<String> {
        return try {
            val items = mutableMapOf<String, Map<String, String>>()
            val docRefAlready = db.collection("workouts_$userId").document(date)
            docRefAlready.get()
                .addOnSuccessListener { document ->
                if (document.exists()){
                    val item = mutableMapOf<String, String>()
                    @Suppress("UNCHECKED_CAST")
                    val contentsMap = document.get("contents") as MutableMap<String, MutableMap<String, String>>
                    for (key in contentsMap.keys){
                        item["時間"] = contentsMap[key]?.get("時間").toString()
                        item["最大心拍"] = contentsMap[key]?.get("最大心拍").toString()
                        item["平均心拍"] = contentsMap[key]?.get("平均心拍").toString()
                        items[key] = item
                    }
                }
                for (i in eventList.indices) {
                    val item = mutableMapOf<String, String>()
                    item["時間"] = minutesList[i]
                    item["最大心拍"] = maxBpmList[i]
                    item["平均心拍"] = avgBpmList[i]
                    items[eventList[i]] = item
                }
                val workoutAdded = Workout(items)

                val docRef = db.collection("workouts_$userId").document(date)
                if (docRef.get().isSuccessful) {
                    docRef.update("contents", workoutAdded.contents)
                } else{
                    docRef.set(workoutAdded)
                }
            }
            .addOnFailureListener { e ->
                ResultTraining.Error(e.message.toString())
            }
            ResultWorkout.Success(userId)
        } catch (e: Exception) {
            Log.d("test", e.stackTraceToString())
            ResultWorkout.Error(e.message.toString())
        }
    }
}