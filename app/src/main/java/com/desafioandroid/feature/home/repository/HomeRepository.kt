package com.desafioandroid.feature.home.repository

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.desafioandroid.core.util.Listing
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.data.source.remote.ApiService
import kotlinx.coroutines.CoroutineScope

class HomeRepository(private val apiService: ApiService) {
    private val config by lazy {
        PagedList.Config.Builder()
            .setPageSize(30)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getPagedItem(scope: CoroutineScope): Listing<Item> {
        val dataSourceFactory = HomeDataSourceFactory(scope, apiService)
        val livePagedList = LivePagedListBuilder(dataSourceFactory, config).build()

        return Listing(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(dataSourceFactory.getLiveDataSource) {
                it.mutableLiveDataNetworkState
            },
            retry = {
                dataSourceFactory.getLiveDataSource.value?.retry?.invoke()
            },
            refresh = {
                dataSourceFactory.getLiveDataSource.value?.invalidate()
            }
        )
    }
}