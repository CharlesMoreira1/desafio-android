package com.desafioandroid.feature.home.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.data.source.remote.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeDataSource(private val scope: CoroutineScope, private val apiService: ApiService) : PageKeyedDataSource<Int, Item>(){

    val mutableLiveDataNetworkState = MutableLiveData<Resource.Status>()
    var retry: () -> Unit = {}

    private var page = 1

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Item>) {
        mutableLiveDataNetworkState.postValue(Resource.Status.LOADING)

        scope.launch {
            try {
                val response = apiService.getListHome(page).items.toMutableList()
                callback.onResult(response, null, ++page)
                mutableLiveDataNetworkState.value = Resource.Status.SUCCESS
            }catch (exception : Exception){
                retry = {
                    loadInitial(params, callback)
                }
                mutableLiveDataNetworkState.value = Resource.Status.ERROR
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        mutableLiveDataNetworkState.postValue(Resource.Status.LOADING_PAGINATION)

        scope.launch {
            try {
                val response = apiService.getListHome(params.key).items.toMutableList()
                callback.onResult(response, params.key + 1)
                mutableLiveDataNetworkState.value = Resource.Status.SUCCESS_PAGINATION
            }catch (t : Throwable){
                when(t) {
                    is HttpException -> {
                        mutableLiveDataNetworkState.value = Resource.Status.END_LIST
                    }
                    else -> {
                        retry = {
                            loadAfter(params, callback)
                        }
                        mutableLiveDataNetworkState.value = Resource.Status.ERROR_PAGINATION
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        //not implemented because we not load backwards
    }
}