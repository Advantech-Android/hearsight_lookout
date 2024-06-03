package com.adv.ilook.model.db.remote.firebase.realtimedatabase

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class FirebaseClient @Inject constructor(databaseReference: DatabaseReference) :
    FireRealTimeDB(databaseReference) {
    fun login(username: String, password: String, callback: (Any?) -> Unit) {

    }

    fun logout(username: String, phone: String, callback: (Any?) -> Unit) {

    }
}