package com.adv.ilook.model.db.remote.repository.apprepo



import android.app.Activity
import com.adv.ilook.model.data.firebasemodel.Responses
import com.adv.ilook.model.data.workflow.Workflow
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import retrofit2.Response


interface CommonRepository {
    suspend fun getWorkflow(): Response<Workflow>?
    suspend fun login(username: String, phone: String,isLogged: Boolean,callback: (Any?) -> Unit): Response<Responses>?
    suspend fun logout(username: String, phone: String,isLogged: Boolean,callback: (Any?) -> Unit): Response<Workflow>?
    fun sendVerificationCode(activity: Activity,
                             phone: String,
                             phoneAuthCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    )
    fun verifyCode(code: String): PhoneAuthCredential


}