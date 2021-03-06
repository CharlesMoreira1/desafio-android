package com.desafioandroid.core.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.desafioandroid.core.helper.Resource

abstract class BaseViewModel : ViewModel() {

    protected fun <T> MutableLiveData<Resource<T>>.success(data: T?) {
        value = Resource.success(data)
    }

    protected fun <T> MutableLiveData<Resource<T>>.error(t: Throwable?) {
        value = Resource.error(t)
    }

    protected fun <T> MutableLiveData<Resource<T>>.loading() {
        value = Resource.loading()
    }
}