package com.desafioandroid.core.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList

enum class StatusPaging { SUCCESS, SUCCESS_AFTER, ERROR, ERROR_AFTER, LOADING, LOADING_AFTER, END_LIST }

data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<StatusPaging>,
    val refresh: () -> Unit,
    val retry: () -> Unit
)

fun <T> MutableLiveData<Listing<T>>.refresh() {
    value?.refresh?.invoke()
}

fun <T> MutableLiveData<Listing<T>>.retry() {
    value?.retry?.invoke()
}

fun <T> MutableLiveData<Listing<T>>.fetchData(data: Listing<T>){
    value = data
}

fun LiveData<StatusPaging>.observeStatusPaging(
    owner: LifecycleOwner,
    onSuccess: () -> Unit,
    onError: () -> Unit,
    onLoading: () -> Unit,
    onOthers: (StatusPaging) -> Unit) {

    observe(owner, Observer { status ->
        when (status) {
            StatusPaging.SUCCESS -> {
                onSuccess.invoke()
            }
            StatusPaging.ERROR -> {
                onError.invoke()
            }
            StatusPaging.LOADING -> {
                onLoading.invoke()
            }
            else -> {
                onOthers.invoke(status)
            }
        }
    })
}