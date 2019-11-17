package com.desafioandroid.feature.home.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.desafioandroid.core.base.BaseViewModel
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.feature.home.repository.HomeRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel() {

    private val mutableLiveDataListItem = MutableLiveData<Resource<MutableList<Item>>>()
    private val itemList = mutableListOf<Item>()
    var currentPage = 1
    var isLoading = true
    var isLastPage = false

    init {
        fetchListItem()
    }

    fun getListItem(): LiveData<Resource<MutableList<Item>>> = mutableLiveDataListItem

    private fun fetchListItem(page: Int = 1) {
        mutableLiveDataListItem.loading()

        viewModelScope.launch {
            try {
                repository.getList(page)?.let {
                    itemList.addAll(it)
                    if (itemList.isNotEmpty()) {
                        mutableLiveDataListItem.success(itemList)
                    }
                }
            } catch (t: Throwable){
                mutableLiveDataListItem.error(t)
            }
        }
    }

    fun nextPage() {
        fetchListItem(++currentPage)
        isLoading = false
    }

    fun backPreviousPage() {
        fetchListItem(currentPage)
    }

    fun refreshViewModel() {
        currentPage = 1
        isLastPage = false
        fetchListItem()
    }

    fun paginationFinished(){
        isLoading = true
        isLastPage = true
    }
}