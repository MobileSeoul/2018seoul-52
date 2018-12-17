package com.project.androidtest.app.home.more.adapter;

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.androidtest.R
import com.project.androidtest.databinding.MoreMyPostItemBinding
import com.project.androidtest.model.post.PostModel

class MyPostAdapter : RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {
    private var items = mutableListOf<PostModel>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: MutableList<PostModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.more_my_post_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyPostAdapter.ViewHolder, position: Int) {
        var item = items[position]
        holder.binding?.post = item
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: MoreMyPostItemBinding? = DataBindingUtil.bind<ViewDataBinding>(itemView) as MoreMyPostItemBinding

    }

    interface OnItemClickListener {
        fun onItemClick(item: PostModel)
    }
}