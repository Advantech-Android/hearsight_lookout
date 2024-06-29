package com.adv.ilook.model.db.remote.firebase.firestore


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.reflect.Type
import javax.inject.Inject

private const val TAG_ = "==>>FireStoreClient"
class FireStoreClient @Inject constructor(
    private val fireStore: FirebaseFirestore,
   private val ioDispatcher: CoroutineDispatcher
) :BaseFireStore(fireStore){
    suspend fun saveUserData(data:List<Any>){

    }
    suspend fun getUserData(phoneNumber:Any,callback:(Any,Any,Boolean)->Unit){
        val customerDetails = fireStore.collection("customer_details").document(phoneNumber.toString())
        customerDetails.get().addOnSuccessListener {result ->
            val gson = Gson()
            var str = ""

            val gsonType: Type? = object : TypeToken<java.util.HashMap<*, *>?>() {}.type
            Log.i(TAG_, "getUserData: id =${result.id} => map =${result.data}")
            str = gson.toJson(result.data, gsonType)
            // var genericModel = gson.fromJson(str, GenericModel::class.java)
            Log.d(TAG_, "getUserData: Map to jsonStr = $str")

            callback(str, result.id,true)
        }.addOnFailureListener {exception ->
            Log.e(TAG, "getUserData: ", )
            callback(getErrorJson(exception),0,false)
        }.addOnCompleteListener { message->
          //  callback(message.toString(), message.result.id)
        }
    }
    fun getErrorJson(exception: Exception): String {
        val errorMap = mapOf("error" to exception.message.toString())
        return Gson().toJson(errorMap)
    }
}