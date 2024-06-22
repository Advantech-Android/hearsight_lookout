package com.adv.ilook.model.db.remote.firebase.realtimedatabase


import com.adv.ilook.model.data.firebasemodel.Responses
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import retrofit2.Response

class FirebaseClient @Inject constructor(databaseReference: DatabaseReference) :
    BaseRealTimeDataBase(databaseReference) {
    fun login(username: String, password: String, callback: (Any?) -> Unit): Response<Responses>? {
        callback("login success")

        return null
    }

    fun logout(username: String, phone: String, callback: (Any?) -> Unit) {

    }
}