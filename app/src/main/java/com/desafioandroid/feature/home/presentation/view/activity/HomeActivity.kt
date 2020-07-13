package com.desafioandroid.feature.home.presentation.view.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.desafioandroid.R
import com.desafioandroid.core.base.BaseActivity
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.core.util.rotationAnimation
import com.desafioandroid.feature.home.presentation.view.adapter.HomeAdapter
import com.desafioandroid.feature.home.presentation.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.layout_reload.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : BaseActivity() {

    private val viewModel by viewModel<HomeViewModel>()

    private val homeAdapter by lazy {
        HomeAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setToolbar(title = R.string.title_toolbar_home, showHomeAsUp = false)

        initViewModel()
        iniUi()
    }

    private fun initViewModel() {
        viewModel.getLiveDataItem.observe(this, Observer {
            homeAdapter.submitList(it)
            swipe_refresh.isRefreshing = false
        })

        viewModel.getNetworkState.observe(this, Observer {
            when (it) {
                Resource.Status.SUCCESS -> {
                    showSuccess()
                }
                Resource.Status.ERROR -> {
                    showError()
                }
                Resource.Status.LOADING -> {
                    showLoading()
                }
                else -> {
                    homeAdapter.setStatus(it)
                    homeAdapter.onRetryClickListener = {
                        viewModel.retry()
                    }
                }
            }
        })
    }

    private fun iniUi() {
        with(recycler_home) {
            this.adapter = homeAdapter
            val linearLayoutManager = LinearLayoutManager(this@HomeActivity)
            this.layoutManager = linearLayoutManager
        }
        swipeRefresh()

    }

    private fun showSuccess() {
        recycler_home.visibility = View.VISIBLE
        include_layout_loading_full_screen.visibility = View.GONE
        include_layout_reload_full_screen.visibility = View.GONE
    }

    private fun showLoading() {
        include_layout_loading_full_screen.visibility = View.VISIBLE
    }

    private fun showError() {
        include_layout_reload_full_screen.visibility = View.VISIBLE
        include_layout_loading_full_screen.visibility = View.GONE
        recycler_home.visibility = View.GONE
        showLoadingOrRetry(false)

        item_bottom.buttonRetry.setOnClickListener { view ->
            view.rotationAnimation()

            viewModel.refresh()
            showLoadingOrRetry(true)
        }
    }

    private fun showLoadingOrRetry(isVisibility: Boolean){
        if (isVisibility) item_bottom.showLoading() else item_bottom.showErrorRetry()
    }

    private fun swipeRefresh() {
        swipe_refresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }
}
