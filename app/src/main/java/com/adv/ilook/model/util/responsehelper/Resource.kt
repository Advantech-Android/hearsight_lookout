package com.adv.ilook.model.util.responsehelper




enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message:Any?
){
    companion object{

        fun <T> success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg:Any, data:T?): Resource<T> {
            return Resource(Status.ERROR, data, msg)
        }

        fun <T> loading(data:T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

    }
}