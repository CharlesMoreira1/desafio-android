package com.desafioandroid.feature.home.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.desafioandroid.core.util.StatusPaging
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.data.source.remote.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeDataSource(private val scope: CoroutineScope, private val apiService: ApiService) : PageKeyedDataSource<Int, Item>(){

    val mutableLiveDataNetworkState = MutableLiveData<StatusPaging>()
    var retry: () -> Unit = {}

    private var page = 1

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Item>) {
        mutableLiveDataNetworkState.postValue(StatusPaging.LOADING)

        scope.launch {
            try {
                val response = apiService.getListHome(page).items.toMutableList()
                callback.onResult(response, null, ++page)
                mutableLiveDataNetworkState.value = StatusPaging.SUCCESS
            }catch (exception : Exception){
                retry = {
                    loadInitial(params, callback)
                }
                mutableLiveDataNetworkState.value = StatusPaging.ERROR
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        mutableLiveDataNetworkState.postValue(StatusPaging.LOADING_AFTER)

        scope.launch {
            try {
                val response = apiService.getListHome(params.key).items.toMutableList()
                callback.onResult(response, params.key + 1)
                mutableLiveDataNetworkState.value = StatusPaging.SUCCESS_AFTER
            }catch (t : Throwable){
                when(t) {
                    is HttpException -> {
                        mutableLiveDataNetworkState.value = StatusPaging.END_LIST
                    }
                    else -> {
                        retry = {
                            loadAfter(params, callback)
                        }
                        mutableLiveDataNetworkState.value = StatusPaging.ERROR_AFTER
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Item>) {
        //not implemented because we not load backwards
    }
}