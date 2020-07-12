package com.desafioandroid.feature.home.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.data.source.remote.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HomeDataSource(private val scope: CoroutineScope, private val apiService: ApiService) : PageKeyedDataSource<Int, Item>(){

    val mutableLiveDataNetworkState = MutableLiveData<Resource<Any>>()
    var retry: () -> Unit = {}

    private var page = 1

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Item>) {
        mutableLiveDataNetworkState.postValue(Resource.loading())

        scope.launch {
            try {
                val response = apiService.getListHome(page).items.toMutableList()
                callback.onResult(response, null, ++page)
                mutableLiveDataNetworkState.value = Resource.success(response)
            }catch (exception : Exception){
                retry = {
                    loadInitial(params, callback)
                }
                mutableLiveDataNetworkState.value = Resource.error(exception)
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        if (params.key > 2) {
            mutableLiveDataNetworkState.postValue(Resource.loadingPagination())
        }
        scope.launch {
            try {
                val response = apiService.getListHome(params.key).items.toMutableList()
                callback.onResult(response, params.key + 1)
            }catch (exception : Exception){
                retry = {
                    loadAfter(params, callback)
                }
                mutableLiveDataNetworkState.value = Resource.errorPagination(exception)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        //not implemented because we not load backwards
    }
}