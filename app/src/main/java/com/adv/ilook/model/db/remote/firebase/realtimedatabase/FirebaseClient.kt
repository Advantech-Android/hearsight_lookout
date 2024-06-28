package com.adv.ilook.model.db.remote.firebase.realtimedatabase


import android.app.Activity
import com.adv.ilook.model.data.firebasemodel.Responses
import com.adv.ilook.model.util.assets.FirebaseKeys.status
import com.adv.ilook.model.util.assets.FirebaseKeys.user_name
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class FirebaseClient @Inject constructor(
    private val databaseReference: DatabaseReference,
    private val auth: FirebaseAuth
) : BaseRealTimeDataBase(databaseReference) {
    private var verificationId: String? = null
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private var rigisteredPhoneNumber by Delegates.notNull<String>()
    private var rigisteredUserName by Delegates.notNull<String>()

    fun login(
        username: String,
        phoneNumber: String,
        status: String,
        isLogged:Boolean,
        callback: (Any?) -> Unit
    ): Response<Responses>? {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(phoneNumber)) {
                    val dbPhoneNumber = snapshot.child(phoneNumber).key.toString()
                    if (phoneNumber == dbPhoneNumber) {
                        val dbUsername =
                            snapshot.child(phoneNumber).child(user_name).value.toString()
                        rigisteredPhoneNumber = phoneNumber
                        rigisteredUserName = dbUsername
                        val dbChild = databaseReference.child(phoneNumber)
                        dbChild.child(status).setValue()
                    }
                } else {

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        callback("login success")

        return null
    }

    fun logout(username: String, phone: String, callback: (Any?) -> Unit) {

    }

    fun sendVerificationCode(
        activity: Activity,
        phone: String,
        phoneAuthCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(phoneAuthCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(code: String): PhoneAuthCredential {
        return PhoneAuthProvider.getCredential(verificationId!!, code)
    }

    fun signInWithCredential(
        credential: PhoneAuthCredential,
        callback: (FirebaseUser?, Exception?) -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(task.result?.user, null)
                } else {
                    callback(null, task.exception)
                }
            }
    }


    fun setVerificationIdAndToken(
        verificationId: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        this.verificationId = verificationId
        this.forceResendingToken = token
    }


}