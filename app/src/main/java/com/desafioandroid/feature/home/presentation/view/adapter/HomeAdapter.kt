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
import com.desafioandroid.core.util.dateFormat
import com.desafioandroid.core.util.decimalFormat
import com.desafioandroid.core.util.fadeAnimation
import com.desafioandroid.data.model.home.entity.Item
import com.desafioandroid.feature.home.presentation.view.adapter.HomeAdapter.ItemViewHolder
import kotlinx.android.synthetic.main.row_data_home.view.*

class HomeAdapter : PagedListAdapter<Item, ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_data_home, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bindView(it) }
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

    companion object {
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