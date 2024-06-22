package com.adv.ilook.model.db.remote.repository.apprepo

import com.adv.ilook.model.data.firebasemodel.Responses
import com.adv.ilook.model.data.workflow.Workflow
import com.adv.ilook.model.db.local.source.CommonDataSource
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.FirebaseClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import javax.inject.Inject


class SeeForMeRepo  @Inject constructor(
    private val local: CommonDataSource,
    private val firebaseClient: FirebaseClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CommonRepository {

    override suspend fun getWorkflow(): Response<Workflow>? {
        return null
    }

    override suspend fun login(
        username: String,
        phone: String,
        callback: (Any?) -> Unit
    ): Response<Responses>? {
        firebaseClient.login(username, phone, callback)
        return null
    }

    override suspend fun logout(
        username: String,
        phone: String,
        callback: (Any?) -> Unit
    ): Response<Workflow>? {
        firebaseClient.logout(username, phone, callback)
        return null
    }

}