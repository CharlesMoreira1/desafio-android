package com.desafioandroid.feature.pullrequest.presentation.view.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafioandroid.R
import com.desafioandroid.core.base.BaseActivity
import com.desafioandroid.core.helper.observeResource
import com.desafioandroid.core.util.addColorSpecificText
import com.desafioandroid.core.util.rotationAnimation
import com.desafioandroid.data.model.pullrequest.entity.PullRequestResponse
import com.desafioandroid.feature.pullrequest.presentation.view.adapter.PullRequestAdapter
import com.desafioandroid.feature.pullrequest.presentation.viewmodel.PullRequestViewModel
import kotlinx.android.synthetic.main.activity_pull_request.*
import kotlinx.android.synthetic.main.layout_reload.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PullRequestActivity : BaseActivity() {

    companion object {
        private const val STATE_OPEN = "open"
        private const val STATE_CLOSED = "closed"
    }

    private val viewModel by viewModel<PullRequestViewModel> {
        parametersOf(nameUser, nameRepository)
    }

    private val pullRequestAdapter by lazy {
        PullRequestAdapter { pullRequestResponse ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pullRequestResponse.htmlUrl)))
        }
    }

    private var nameUser: String = ""
    private var nameRepository: String = ""
    private var stateOpen: Int = 0
    private var stateClosed: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pull_request)

        setToolbar(title = R.string.title_toolbar_pull_request, showHomeAsUp = true)

        catchIntent()

        initViewModel()
        iniUi()
    }

    private fun catchIntent() {
        intent?.let {
            nameUser = it.getStringExtra("name_user")
            nameRepository = it.getStringExtra("name_repository")
        }
    }

    private fun initViewModel() {
        viewModel.getListPullRequest().observeResource(this,
            onSuccess = {
                populateList(it)
                populateStatePullRequest(it)
                showSuccess()
            },
            onLoading = {
                showLoading()
            },
            onError = {
                showError()
            }
        )
    }

    private fun iniUi() {
        with(recycler_pull_request) {
            this.adapter = pullRequestAdapter
            val linearLayoutManager = LinearLayoutManager(this@PullRequestActivity)
            this.layoutManager = linearLayoutManager
        }

        swipeRefresh()
    }

    private fun populateList(pullRequestList: List<PullRequestResponse>) {
        pullRequestAdapter.addList(pullRequestList)
        showListEmpty(pullRequestList)
    }

    private fun populateStatePullRequest(pullRequestList: List<PullRequestResponse>) {
        pullRequestList.forEach {
            when(it.state){
                STATE_OPEN -> stateOpen++
                STATE_CLOSED -> stateClosed++
            }
        }

        val stateOpenString = getString(R.string.message_state_open_pull_request, stateOpen)
        val stateClosedString = getString(R.string.message_state_closed_pull_request, stateClosed)

        text_total_state.text = getString(R.string.division_concat, stateOpenString, stateClosedString)
            .addColorSpecificText(this, R.color.colorOrange, stateOpenString)
    }

    private fun refresh() {
        stateOpen = 0
        stateClosed = 0
        pullRequestAdapter.clearList()
        viewModel.refreshViewModel()
    }

    private fun swipeRefresh() {
        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed({
                refresh()

                swipe_refresh.isRefreshing = false
            }, 1000)
        }
    }

    private fun showSuccess() {
        recycler_pull_request.visibility = View.VISIBLE
        card_total_state.visibility = View.VISIBLE
        include_layout_loading.visibility = View.GONE
        include_layout_reload.visibility = View.GONE
    }

    private fun showLoading() {
        include_layout_loading.visibility = View.VISIBLE
    }

    private fun showError() {
        include_layout_reload.visibility = View.VISIBLE
        include_layout_loading.visibility = View.GONE
        recycler_pull_request.visibility = View.GONE
        card_total_state.visibility = View.GONE

        showLoadingAndHideButtonRefresh(false)

        item_bottom.buttonRetry.setOnClickListener { view ->
            view.rotationAnimation()

            refresh()

            showLoadingAndHideButtonRefresh(true)
            include_layout_loading.visibility = View.GONE
        }
    }

    private fun showListEmpty(pullRequestList: List<PullRequestResponse>) {
        val listIsEmpty = pullRequestList.isEmpty()
        val listEmptyVisibilityVisible = if (listIsEmpty) View.VISIBLE else View.GONE
        val listEmptyVisibilityGone = if (listIsEmpty) View.GONE else View.VISIBLE

        include_layout_empty.visibility = listEmptyVisibilityVisible
        swipe_refresh.visibility = listEmptyVisibilityGone
        card_total_state.visibility = listEmptyVisibilityGone
    }

    private fun showLoadingAndHideButtonRefresh(isVisibility: Boolean) {
        if (isVisibility) item_bottom.showLoading() else item_bottom.showErrorRetry()
    }
}
