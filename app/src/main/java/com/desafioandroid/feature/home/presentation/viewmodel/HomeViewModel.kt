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
    val totalItem: Int = 29
    var currentPage = 2
    var releasedLoad: Boolean = true

    val getListItem: LiveData<Resource<MutableList<Item>>> by lazy {
        fetchListItem()
        return@lazy mutableLiveDataListItem
    }

    private fun fetchListItem(page: Int = 1) {
        mutableLiveDataListItem.loading()
        viewModelScope.launch {
            try {
                mutableLiveDataListItem.success(repository.getList(page)?.let { it })
            } catch (e: Exception) {
                mutableLiveDataListItem.error(e)
            }
        }
    }

    fun nextPage() {
        fetchListItem(currentPage++)
        releasedLoad = false
    }

    fun backPreviousPage() {
        fetchListItem(currentPage-1)
    }

    fun refreshViewModel() {
        currentPage = 2
        fetchListItem()
    }
}