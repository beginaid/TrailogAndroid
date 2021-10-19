package com.codaid.trailogandroid.main.add_weight

import com.codaid.trailogandroid.common.ResultWeight
import com.codaid.trailogandroid.common.custom_model.Weight
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class WeightDataSource {
    private val db = Firebase.firestore

    fun addWeight(userId: String?, date: String, weight: Float): ResultWeight<String> {
        return try {
            val weightAdded = Weight(weight)
            db.collection("weights_$userId").document(date).set(weightAdded)
            ResultWeight.Success(userId)
        } catch (e: Exception) {
            ResultWeight.Error(e.message.toString())
        }
    }

    fun deleteWeight(userId: String?, date: String): ResultWeight<String> {
        return try {
            db.collection("weights_$userId").document(date).delete()
            ResultWeight.Success(userId)
        } catch (e: Exception) {
                ResultWeight.Error(e.message.toString())
        }
    }
}