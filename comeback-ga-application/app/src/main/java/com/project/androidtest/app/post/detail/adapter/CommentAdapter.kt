package com.project.androidtest.app.post.detail.adapter;

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.androidtest.R
import com.project.androidtest.databinding.PostDetailCommentItemBinding
import com.project.androidtest.model.comment.CommentModel

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    private var items = mutableListOf<CommentModel>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: MutableList<CommentModel>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_detail_comment_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        var item = items[position]
        holder.binding?.comment = item
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: PostDetailCommentItemBinding? = DataBindingUtil.bind<ViewDataBinding>(itemView) as PostDetailCommentItemBinding

    }

    interface OnItemClickListener {
        fun onItemClick(item: CommentModel)
    }
}