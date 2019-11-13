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
import kotlinx.android.synthetic.main.layout_reload_bottom.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    private val homeAdapter by lazy {
        HomeAdapter { item ->
            if (viewModel.releasedLoad) {
                val intent = Intent(this@HomeActivity, PullRequestActivity::class.java)
                intent.putExtra("name_user", item.owner.login)
                intent.putExtra("name_repository", item.name)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setToolbar(title = R.string.title_toolbar_home, showHomeAsUp = false)

        initViewModel()
        iniUi()
    }

    private fun initViewModel() {
        viewModel.getListItem.observeResource(this,
            onSuccess = {
                populateList(it)
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
        with(recycler_home) {
            this.adapter = homeAdapter
            val linearLayoutManager = LinearLayoutManager(this@HomeActivity)
            this.addOnScrollListener(object : PaginationScroll(linearLayoutManager) {
                override fun loadMoreItems() {
                    viewModel.nextPage()
                }

                override fun isLoading(): Boolean {
                    return viewModel.releasedLoad
                }

                override fun hideMoreItems() {
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
                if (viewModel.releasedLoad) {
                    refresh()
                }
                swipe_refresh.isRefreshing = false
            }, 1000)
        }
    }

    private fun showSuccess() {
        recycler_home.visibility = View.VISIBLE
        include_layout_loading_full_screen.visibility = View.GONE
        include_layout_reload_full_screen.visibility = View.GONE
        viewModel.releasedLoad = true
    }

    private fun showLoading() {
        include_layout_loading_full_screen.visibility = View.VISIBLE
    }

    private fun showError() {
        if (viewModel.currentPage > 1) { errorBottomScroll() } else { errorFullScreen() }
        include_layout_loading_full_screen.visibility = View.GONE
    }

    private fun errorFullScreen() {
        include_layout_reload_full_screen.visibility = View.VISIBLE
        recycler_home.visibility = View.GONE

        showLoadingAndHideButtonRefreshFullScreen(false)

        image_refresh_full_screen.setOnClickListener { view ->
            view.rotationAnimation()

            refresh()

            showLoadingAndHideButtonRefreshFullScreen(true)
            include_layout_loading_full_screen.visibility = View.GONE
        }
    }

    private fun errorBottomScroll() {
        showLoadingAndHideButtonRefreshBottomScroll(false)

        image_refresh_bottom_default.rotationAnimation().setOnClickListener {
            viewModel.backPreviousPage()

            showLoadingAndHideButtonRefreshBottomScroll(true)
            include_layout_loading_full_screen.visibility = View.GONE
        }
    }

    private fun showLoadingAndHideButtonRefreshFullScreen(isVisibility: Boolean) {
        progress_loading_full_screen.visibility = if (isVisibility) View.VISIBLE else View.GONE
        image_refresh_full_screen.visibility = if (isVisibility) View.GONE else View.VISIBLE
    }

    private fun showLoadingAndHideButtonRefreshBottomScroll(isVisibility: Boolean) {
    }
}
