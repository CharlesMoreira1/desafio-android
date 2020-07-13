package com.desafioandroid.feature.home.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.desafioandroid.core.base.BaseViewModel
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.core.util.Listing
import com.desafioandroid.core.util.fetchData
import com.desafioandroid.core.util.refresh
import com.desafioandroid.core.util.retry
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.feature.home.repository.HomeRepository

class HomeViewModel(repository: HomeRepository) : BaseViewModel() {
    private val mutableLiveDataListingItem = MutableLiveData<Listing<Item>>()
    val getLiveDataItem: LiveData<PagedList<Item>> = switchMap(mutableLiveDataListingItem){ it.pagedList }
    val getNetworkState: LiveData<Resource.Status> = switchMap(mutableLiveDataListingItem){ it.networkState }

    init {
        mutableLiveDataListingItem.fetchData(repository.getPagedItem(viewModelScope))
    }

    fun refresh() {
        mutableLiveDataListingItem.refresh()
    }

    fun retry() {
        mutableLiveDataListingItem.retry()
    }
}