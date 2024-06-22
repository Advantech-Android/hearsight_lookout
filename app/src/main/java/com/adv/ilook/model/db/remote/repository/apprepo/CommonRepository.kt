package com.adv.ilook.model.db.remote.repository.apprepo



import com.adv.ilook.model.data.firebasemodel.Responses
import com.adv.ilook.model.data.workflow.Workflow
import retrofit2.Response


interface CommonRepository {
    suspend fun getWorkflow(): Response<Workflow>?
    suspend fun login(username: String, phone: String,callback: (Any?) -> Unit): Response<Responses>?
    suspend fun logout(username: String, phone: String,callback: (Any?) -> Unit): Response<Workflow>?

}