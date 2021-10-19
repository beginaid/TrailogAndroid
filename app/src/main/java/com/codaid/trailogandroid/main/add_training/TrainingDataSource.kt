package com.codaid.trailogandroid.main.add_training

import com.codaid.trailogandroid.common.ResultTraining
import com.codaid.trailogandroid.common.custom_model.Training
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TrainingDataSource {
    private val db = Firebase.firestore

    fun addTraining(userId: String?, date: String, eventList: MutableList<String>, weightList: MutableList<String>, repsList: MutableList<String>): ResultTraining<String> {
        return try {
            val items = mutableMapOf<String, Map<String, String>>()
            val docRefAlready = db.collection("trainings_$userId").document(date)
            docRefAlready.get()
                .addOnSuccessListener { document ->
                    if (document.exists()){
                        val item = mutableMapOf<String, String>()
                        @Suppress("UNCHECKED_CAST")
                        val contentsMap = document.get("contents") as MutableMap<String, MutableMap<String, String>>
                        for (key in contentsMap.keys){
                            item["負荷"] = contentsMap[key]?.get("負荷").toString()
                            item["回数"] = contentsMap[key]?.get("回数").toString()
                            items[key] = item
                        }
                    }
                    for (i in eventList.indices) {
                        val item = mutableMapOf<String, String>()
                        item["負荷"] = weightList[i]
                        item["回数"] = repsList[i]
                        items[eventList[i]] = item
                    }
                    val trainingAdded = Training(items)

                    val docRef = db.collection("trainings_$userId").document(date)
                    if (docRef.get().isSuccessful) {
                        docRef.update("contents", trainingAdded.contents)
                    } else{
                        docRef.set(trainingAdded)
                    }
                }
                .addOnFailureListener { e ->
                    ResultTraining.Error(e.message.toString())
                }
            ResultTraining.Success(userId)
        } catch (e: Exception) {
            ResultTraining.Error(e.message.toString())
        }
    }

}