package com.project.androidtest.app.home.like.adapter;

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.androidtest.model.post.PostModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.project.androidtest.R
import com.project.androidtest.databinding.LikePostItemBinding
import com.project.androidtest.model.like.LikeModel
import com.project.androidtest.util.SERVER_URL


class PostLikeAdapter : RecyclerView.Adapter<PostLikeAdapter.ViewHolder>() {
    private var items = mutableListOf<LikeModel>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: MutableList<LikeModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostLikeAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.like_post_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PostLikeAdapter.ViewHolder, position: Int) {
        var item = items[position]
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item.post)
        }
        holder.binding?.post = item.post
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: LikePostItemBinding? = DataBindingUtil.bind<ViewDataBinding>(itemView) as LikePostItemBinding
    }

    interface OnItemClickListener {
        fun onItemClick(item: PostModel)
    }
}