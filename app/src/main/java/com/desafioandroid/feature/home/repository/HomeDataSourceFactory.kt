package com.desafioandroid.feature.home.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.data.source.remote.ApiService
import kotlinx.coroutines.CoroutineScope

class HomeDataSourceFactory(private val scope: CoroutineScope, private val apiService: ApiService) : DataSource.Factory<Int, Item>() {
    private val mutableLiveDataSource = MutableLiveData<HomeDataSource>()
    val getLiveDataSource: LiveData<HomeDataSource> = mutableLiveDataSource

    override fun create(): DataSource<Int, Item> {
        val homeDataSource = HomeDataSource(scope, apiService)
        mutableLiveDataSource.postValue(homeDataSource)
        return homeDataSource
    }
}