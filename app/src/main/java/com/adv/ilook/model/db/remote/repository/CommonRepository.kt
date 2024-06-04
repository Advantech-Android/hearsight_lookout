package com.adv.ilook.model.db.remote.repository



import com.adv.ilook.model.data.workflow.Workflow
import retrofit2.Response


interface CommonRepository {
    suspend fun getWorkflow(): Response<Workflow>?
    suspend fun login(username: String, phone: String,callback: (Any?) -> Unit): Response<Workflow>?
    suspend fun logout(username: String, phone: String,callback: (Any?) -> Unit): Response<Workflow>?

}