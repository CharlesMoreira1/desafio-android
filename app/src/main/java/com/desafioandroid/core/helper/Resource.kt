package com.desafioandroid.core.helper

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

data class Resource<out T>(val status: Status, val data: T?, val throwable: Throwable?) {

    enum class Status { SUCCESS, SUCCESS_PAGINATION, ERROR, ERROR_PAGINATION, LOADING, LOADING_PAGINATION, END_LIST }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(throwable: Throwable?): Resource<T> {
            return Resource(Status.ERROR, null, throwable)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null,  null)
        }
    }
}

fun <T> LiveData<Resource<T>>.observeResource(
    owner: LifecycleOwner,
    onSuccess: (T) -> Unit,
    onError: (Throwable) -> Unit,
    onLoading: () -> Unit) {

    observe(owner, Observer { resource ->
        when (resource.status) {
            Resource.Status.SUCCESS -> {
                resource.data?.let {
                    onSuccess.invoke(it)
                }
            }
            Resource.Status.ERROR -> {
                resource.throwable?.let {
                    onError.invoke(it)
                }
            }
            Resource.Status.LOADING -> {
                onLoading.invoke()
            }
            else -> {
                //Does nothing
            }
        }
    })
}