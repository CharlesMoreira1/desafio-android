package com.desafioandroid.feature.home.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.desafioandroid.R
import com.desafioandroid.core.customview.ItemReload
import com.desafioandroid.core.helper.Resource
import com.desafioandroid.core.util.dateFormat
import com.desafioandroid.core.util.decimalFormat
import com.desafioandroid.core.util.fadeAnimation
import com.desafioandroid.data.model.home.entity.Item
import kotlinx.android.synthetic.main.layout_item_bottom.view.*
import kotlinx.android.synthetic.main.row_data_home.view.*

class HomeAdapter : PagedListAdapter<Item, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private var status: Resource.Status? = null
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

    fun setStatus(status: Resource.Status) {
        this.status = status

        isLoading = if (status == Resource.Status.LOADING_PAGINATION || status == Resource.Status.ERROR_PAGINATION) {
            notifyItemChanged(super.getItemCount())
            true
        } else {
            notifyItemRemoved(itemCount-1)
            false
        }
    }

    inner class ItemViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val nameRepository: TextView = view.text_name_repository
        private val description: TextView = view.text_description
        private val countFork: TextView = view.text_count_fork
        private val countStar: TextView = view.text_count_star
        private val nameUser: TextView = view.text_name_user
        private val createdDate: TextView = view.text_created_date
        private val imageAvatar: ImageView = view.image_avatar

        fun bindView(dataItem: Item) = with(view) {
            nameRepository.text = dataItem.fullName
            description.text = dataItem.description
            countFork.text = dataItem.forksCount.decimalFormat()
            countStar.text = dataItem.stargazersCount.decimalFormat()
            nameUser.text = dataItem.owner.login
            createdDate.text = dataItem.createdAt.dateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

            imageAvatar.fadeAnimation()

            val drawableImageDefault = R.drawable.icon_github_avatar_preview

            Glide.with(context)
                .load(dataItem.owner.avatarUrl)
                .placeholder(drawableImageDefault)
                .error(drawableImageDefault)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageAvatar)
        }
    }

    inner class ItemBottomViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {

        private val itemBottom: ItemReload = view.item_bottom

        fun bindView() = with(view) {
            if (status == Resource.Status.ERROR_PAGINATION) {
                itemBottom.showErrorRetry()

                itemBottom.buttonRetry.setOnClickListener {
                    onRetryClickListener.invoke()
                }
            } else {
                itemBottom.showLoading()
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