package com.desafioandroid.feature.home.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.desafioandroid.R
import com.desafioandroid.core.util.StatusPaging
import com.desafioandroid.core.util.dateFormat
import com.desafioandroid.core.util.decimalFormat
import com.desafioandroid.core.util.fadeAnimation
import com.desafioandroid.data.model.home.entity.Item
import kotlinx.android.synthetic.main.layout_item_bottom.view.*
import kotlinx.android.synthetic.main.row_data_home.view.*

class HomeAdapter : PagedListAdapter<Item, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var statusPaging: StatusPaging? = null
    private var isLoading = false

    var onRetryClickListener: () -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_LIST) {
            ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_data_home, parent, false))
        } else {
            ItemBottomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_bottom, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            ITEM_LIST -> {
                val newHolder = holder as ItemViewHolder
                getItem(position)?.let { newHolder.bindView(it) }
            }
            ITEM_BOTTOM -> {
                val newHolder = holder as ItemBottomViewHolder
                newHolder.bindView()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) {
            super.getItemCount() + 1
        } else {
            super.getItemCount()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && (position == itemCount-1)) ITEM_BOTTOM else ITEM_LIST
    }

    fun setStatus(statusPaging: StatusPaging) {
        this.statusPaging = statusPaging

        isLoading = if (statusPaging == StatusPaging.LOADING_AFTER || statusPaging == StatusPaging.ERROR_AFTER) {
            notifyItemChanged(super.getItemCount())
            true
        } else {
            notifyItemRemoved(itemCount-1)
            false
        }
    }

    inner class ItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView(dataItem: Item) = with(view) {
            text_name_repository.text = dataItem.fullName
            text_description.text = dataItem.description
            text_count_fork.text = dataItem.forksCount.decimalFormat()
            text_count_star.text = dataItem.stargazersCount.decimalFormat()
            text_name_user.text = dataItem.owner.login
            text_created_date.text = dataItem.createdAt.dateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

            image_avatar.fadeAnimation()

            val drawableImageDefault = R.drawable.icon_github_avatar_preview

            Glide.with(context)
                .load(dataItem.owner.avatarUrl)
                .placeholder(drawableImageDefault)
                .error(drawableImageDefault)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(image_avatar)
        }
    }

    inner class ItemBottomViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        fun bindView() = with(view) {
            if (statusPaging == StatusPaging.ERROR_AFTER) {
                item_bottom.showErrorRetry()

                item_bottom.buttonRetry.setOnClickListener {
                    onRetryClickListener.invoke()
                }
            } else {
                item_bottom.showLoading()
            }
        }
    }

    companion object {
        const val ITEM_LIST = 0
        const val ITEM_BOTTOM = 1

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}