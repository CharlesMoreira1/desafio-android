package com.desafioandroid.feature.pullrequest.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.desafioandroid.core.base.BaseViewModel
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.data.model.pullrequest.entity.PullRequestResponse
import com.desafioandroid.feature.pullrequest.repository.PullRequestRepository
import kotlinx.coroutines.launch

class PullRequestViewModel(private val userName: String, private val repositoryName: String,
                           private val repository: PullRequestRepository) : BaseViewModel() {

    private val mutableLiveDataListPullRequest = MutableLiveData<Resource<List<PullRequestResponse>>>()

    init {
        fetchPullRequest()
    }

    fun getListPullRequest(): LiveData<Resource<List<PullRequestResponse>>> = mutableLiveDataListPullRequest

    private fun fetchPullRequest() {
        viewModelScope.launch {
            mutableLiveDataListPullRequest.loading()
            try {
                mutableLiveDataListPullRequest.success(repository.getList(userName, repositoryName)?.let { it })
            } catch (e: Exception) {
                mutableLiveDataListPullRequest.error(e)
            }
        }
    }

    fun refreshViewModel(){
        fetchPullRequest()
    }
}

