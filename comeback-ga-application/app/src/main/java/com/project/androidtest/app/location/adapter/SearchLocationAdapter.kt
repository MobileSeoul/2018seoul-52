package com.project.androidtest.app.location.adapter;

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.androidtest.R
import com.project.androidtest.databinding.SearchLocationItemBinding
import com.project.androidtest.model.kakao.PlaceModel

class SearchLocationAdapter : RecyclerView.Adapter<SearchLocationAdapter.ViewHolder>() {
    private var items = mutableListOf<PlaceModel>()
    private lateinit var onItemClickListener: OnItemClickListener

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: MutableList<PlaceModel>) {
        this.items.clear()
        var filteredItems = items.filter {
            it.roadAddressName != ""
        }
        this.items.addAll(filteredItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.search_location_item, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SearchLocationAdapter.ViewHolder, position: Int) {
        var item = items[position]
        holder.binding.place = item
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding: SearchLocationItemBinding = DataBindingUtil.bind<ViewDataBinding>(itemView) as SearchLocationItemBinding
    }

    interface OnItemClickListener {
        fun onItemClick(item: PlaceModel)
    }
}