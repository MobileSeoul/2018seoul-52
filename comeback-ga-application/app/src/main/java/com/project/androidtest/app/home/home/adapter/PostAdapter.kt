package com.project.androidtest.app.home.home.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.androidtest.R
import com.project.androidtest.model.post.PostModel
import com.project.androidtest.util.SERVER_URL
import kotlinx.android.synthetic.main.home_content_post_item.view.*

class PostAdapter: RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var items = mutableListOf<PostModel>()
    private var originItems = mutableListOf<PostModel>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: MutableList<PostModel>) {
        this.items.clear()
        this.items.addAll(items)
        this.originItems = this.items
        notifyDataSetChanged()
    }

    fun searchItems(query: String) {
        var resultItems = this.originItems.filter {
            it.description.contains(query) or it.location.contains(query) or it.description.contains(query)
        }
        this.items = resultItems as MutableList<PostModel>
        notifyDataSetChanged()
    }

    fun categoryFilter(query: String) {
        var resultItems = this.originItems.filter {
            it.category.contains(query)
        }
        this.items = resultItems as MutableList<PostModel>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.PostViewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_content_post_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PostAdapter.PostViewHolder, position: Int) {
        var item = items[position]
        holder.bindItem(item)

        if (position == 0) holder.itemView.space_home_content_post_item_horizontal_start.visibility = View.VISIBLE
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }

    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private var item: PostModel? = null

        fun bindItem(item: PostModel) {
            this.item = item
            itemView.apply {
                iv_home_content_post_item_poster.setImageURI(item.poster)
                iv_home_content_post_item_profile.setImageURI(item.user.poster)
                tv_home_content_post_item_user.text = item.user.nickname
                tv_home_content_post_item_location.text = item.location
                tv_home_content_post_item_description.text = item.description
                tv_home_post_item_title.text = item.title
                tv_home_post_item_category.text = item.category
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: PostModel)
    }
}