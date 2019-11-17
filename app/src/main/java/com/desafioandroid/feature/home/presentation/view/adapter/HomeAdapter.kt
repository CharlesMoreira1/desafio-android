package com.desafioandroid.feature.home.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.desafioandroid.R
import com.desafioandroid.core.util.*
import com.desafioandroid.core.customview.ItemReload
import com.desafioandroid.data.model.home.entity.Item
import kotlinx.android.synthetic.main.layout_item_bottom.view.*
import kotlinx.android.synthetic.main.row_data_home.view.*
import kotlin.collections.ArrayList

class HomeAdapter(private val onItemClickListener: ((Item) -> Unit), private val onRetryClickListener: (() -> Unit)) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_LIST = 0
        const val ITEM_BOTTOM = 1
    }

    private var listItem = ArrayList<Item>()
    private var isLoadingAdded = false
    private var retryPageLoad = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return if (p1 == ITEM_LIST) {
            ItemViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.row_data_home, p0, false), onItemClickListener)
        } else {
            ItemBottomViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.layout_item_bottom, p0, false), onRetryClickListener)
        }
    }

    override fun getItemCount(): Int {
        return if (isLoadingAdded) {
            listItem.size + 1
        } else {
            listItem.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listItem.size) ITEM_LIST else ITEM_BOTTOM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p1: Int) {
        when (holder) {
            is ItemViewHolder -> {
                val dataItem = listItem[p1]
                holder.bindView(dataItem)
            }
            is ItemBottomViewHolder -> {
                holder.bindView(retryPageLoad)
            }
        }
    }

    fun addList(dataItem: List<Item>) {
        val initPosition = this.listItem.size
        this.listItem = dataItem as ArrayList<Item>

        notifyItemRangeInserted(initPosition, this.listItem.size)
        addItemBottom()
    }

    fun clearList() {
        isLoadingAdded = false
        this.listItem.clear()
        notifyDataSetChanged()
    }

    fun addItemBottom() {
        isLoadingAdded = true
    }

    fun removeItemBottom() {
        isLoadingAdded = false
        notifyItemRangeRemoved(this.listItem.size, 1)
    }

    fun showErrorRetry(showError: Boolean) {
        retryPageLoad = showError
        notifyItemChanged(this.listItem.size, 1)
    }

    class ItemViewHolder(private val view: View, private val onItemClickListener: ((Item) -> Unit)) :
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

            this.setOnClickListener {
                onItemClickListener.invoke(dataItem)
            }
        }
    }

    class ItemBottomViewHolder(private val view: View, private val onRetryClickListener: (() -> Unit)) :
        RecyclerView.ViewHolder(view) {

        private val itemBottom: ItemReload = view.item_bottom

        fun bindView(retryPageLoad: Boolean) = with(view) {
            if (retryPageLoad) {
                itemBottom.showErrorRetry()
            } else {
                itemBottom.showLoading()
            }

            itemBottom.buttonRetry.setOnClickListener {
                onRetryClickListener.invoke()
            }
        }
    }
}