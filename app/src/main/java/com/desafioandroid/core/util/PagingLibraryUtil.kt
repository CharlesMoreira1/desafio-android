package com.desafioandroid.core.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.desafioandroid.core.helper.Resource

data class Listing<T>(
    val pagedList: LiveData<PagedList<T>>,
    val networkState: LiveData<Resource<Any>>,
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