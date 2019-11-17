package com.desafioandroid.feature.home.presentation.view.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafioandroid.R
import com.desafioandroid.core.base.BaseActivity
import com.desafioandroid.core.helper.PaginationScroll
import com.desafioandroid.core.helper.observeResource
import com.desafioandroid.core.util.rotationAnimation
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.feature.home.presentation.view.adapter.HomeAdapter
import com.desafioandroid.feature.home.presentation.viewmodel.HomeViewModel
import com.desafioandroid.feature.pullrequest.presentation.view.activity.PullRequestActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_reload.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.HttpException

class HomeActivity : BaseActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    private val homeAdapter by lazy {
        HomeAdapter(
            onItemClickListener = { item ->
                val intent = Intent(this@HomeActivity, PullRequestActivity::class.java)
                intent.putExtra("name_user", item.owner.login)
                intent.putExtra("name_repository", item.name)
                startActivity(intent)
            },
            onRetryClickListener = {
                errorBottomScroll(false)
                viewModel.backPreviousPage()
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setToolbar(title = R.string.title_toolbar_home, showHomeAsUp = false)

        initViewModel()
        iniUi()
    }

    private fun initViewModel() {
        viewModel.getListItem().observeResource(this,
            onSuccess = {
                populateList(it)
                showSuccess()
            },
            onLoading = {
                if (viewModel.isLoading) {
                    showLoading()
                }
            },
            onError = {
                showError(it)
            }
        )
    }

    private fun iniUi() {
        with(recycler_home) {
            this.adapter = homeAdapter
            val linearLayoutManager = LinearLayoutManager(this@HomeActivity)
            this.addOnScrollListener(object : PaginationScroll(linearLayoutManager) {
                override fun loadMoreItems() {
                    viewModel.nextPage()
                }

                override fun isLoading(): Boolean {
                    return viewModel.isLoading
                }

                override fun getTotalPageCount(): Int {
                    return 0
                }

                override fun isLastPage(): Boolean {
                    return viewModel.isLastPage
                }

                override fun hideOthersItems() {
                    include_layout_loading_full_screen.visibility = View.GONE
                }
            })

            this.layoutManager = linearLayoutManager
        }

        swipeRefresh()
    }

    private fun populateList(itemList: List<Item>) {
        homeAdapter.addList(itemList)
    }

    private fun refresh() {
        homeAdapter.clearList()
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
        recycler_home.visibility = View.VISIBLE
        include_layout_loading_full_screen.visibility = View.GONE
        include_layout_reload_full_screen.visibility = View.GONE
        viewModel.isLoading = true
    }

    private fun showLoading() {
        include_layout_loading_full_screen.visibility = View.VISIBLE
    }

    private fun showError(t: Throwable){
        when(t){
            is HttpException -> {
                if (t.code() == 422){
                    paginationFinished()
                }
            }
            is Exception -> {
                if (viewModel.currentPage > 1) {
                    errorBottomScroll(true)
                } else {
                    errorFullScreen()
                }
            }
        }
    }

    private fun errorFullScreen() {
        showLoadingAndHideButtonRefreshFullScreen(false)

        item_bottom.buttonRetry.setOnClickListener { view ->
            view.rotationAnimation()

            refresh()
            showLoadingAndHideButtonRefreshFullScreen(true)
        }
    }

    private fun errorBottomScroll(showError: Boolean) {
        homeAdapter.showErrorRetry(showError)
    }

    private fun showLoadingAndHideButtonRefreshFullScreen(isVisibility: Boolean) {
        include_layout_reload_full_screen.visibility = View.VISIBLE
        include_layout_loading_full_screen.visibility = View.GONE
        recycler_home.visibility = View.GONE

        if (isVisibility) item_bottom.showLoading() else item_bottom.showErrorRetry()
    }

    private fun paginationFinished() {
        viewModel.paginationFinished()
        homeAdapter.removeItemBottom()
    }
}
