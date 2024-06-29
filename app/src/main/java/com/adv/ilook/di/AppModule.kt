package com.adv.ilook.di

import android.content.Context
import com.adv.ilook.model.db.local.source.CommonDataSource
import com.adv.ilook.model.db.local.source.LocalDataSource
import com.adv.ilook.model.db.remote.firebase.firestore.BaseFireStore
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreClient
import com.adv.ilook.model.db.remote.firebase.firestore.FireStoreImpl
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.BaseRealTimeDataBase
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.FirebaseClient
import com.adv.ilook.model.db.remote.firebase.realtimedatabase.RealTimeDBImpl
import com.adv.ilook.model.db.remote.repository.apprepo.CommonRepository
import com.adv.ilook.model.db.remote.repository.apprepo.SeeForMeRepo
import com.adv.ilook.model.db.remote.repository.service.MainService
import com.adv.ilook.model.util.assets.IPref
import com.adv.ilook.model.util.assets.PrefImpl
import com.adv.ilook.view.base.BasicFunction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideMainService(): MainService {
        return MainService()
    }
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
    @Singleton
    @Provides
    fun provideMainDispatcher() = Dispatchers.Main
    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    @Provides
    fun provideGetContext(@ApplicationContext context: Context):Context=context

    @Provides
    fun provideBasicFunction(@ApplicationContext context: Context): BasicFunction {
        return BasicFunction(context)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Singleton
    @Provides
    fun provideJsonObj(): JSONObject = JSONObject()



    @Singleton
    @Provides
    fun provideJsonArray(): JSONArray = JSONArray()

    // TODO: 05-28-2024 FirebaseFireStore
    @Singleton
    @Provides
    fun provideFireStoreDatabase(): FirebaseFirestore = FirebaseFirestore.getInstance()
    @Singleton
    @Provides
    fun provideFireStoreImpl(db: FirebaseFirestore): /*FireStoreDatabase is Base ABS class class*/ BaseFireStore =
        FireStoreImpl(db)

    @Singleton
    @Provides
    fun provideFireStoreClient(db: FirebaseFirestore,ioDispatcher: CoroutineDispatcher): /*FireStoreDatabase is Base ABS class class*/ BaseFireStore =
        FireStoreClient(db,ioDispatcher)

    // TODO: 05-28-2024 FirebaseRealTimeDB
    @Singleton
    @Provides
    fun provideFireDatabaseInstance(): FirebaseDatabase = FirebaseDatabase.getInstance()
    @Singleton
    @Provides
    fun provideFireAuthInstance(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFireDatabaseRef(): DatabaseReference = Firebase.database.reference
    @Singleton
    @Provides
    fun provideFireRealTimeDBImpl(db: DatabaseReference):

            /*FireRealTimeDB is Base ABS class class*/BaseRealTimeDataBase = RealTimeDBImpl(db)

    @Singleton
    @Provides
    fun provideFirebaseClientImpl(db: DatabaseReference,auth: FirebaseAuth,fireStoreClient:FireStoreClient,ioDispatcher: CoroutineDispatcher):

            /*FireRealTimeDB is Base ABS class class*/FirebaseClient = FirebaseClient(db,auth,fireStoreClient,ioDispatcher)
    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalDataSource
    @Singleton
    @LocalDataSource
    @Provides
    fun provideLocalDataSource(ioDispatcher:CoroutineDispatcher):CommonDataSource=LocalDataSource(ioDispatcher)
    @Provides
    fun provideCommonRepository(@AppModule.LocalDataSource local: CommonDataSource,firebaseClient: FirebaseClient,fireStoreClient: FireStoreClient,ioDispatcher:CoroutineDispatcher): CommonRepository {
        return SeeForMeRepo(local,firebaseClient,fireStoreClient,ioDispatcher)
    }
    @Singleton
    @Provides
    fun provideSeeForMeRepo( @AppModule.LocalDataSource local: CommonDataSource,firebaseClient: FirebaseClient,fireStoreClient: FireStoreClient,ioDispatcher:CoroutineDispatcher): SeeForMeRepo =
        SeeForMeRepo(local,firebaseClient,fireStoreClient,ioDispatcher)
}

@Module
@InstallIn(SingletonComponent::class)

abstract class ResourceWrapperModule {
      @Singleton
    @Binds
    abstract fun bindPrefImpl(prefImpl: PrefImpl): IPref
}