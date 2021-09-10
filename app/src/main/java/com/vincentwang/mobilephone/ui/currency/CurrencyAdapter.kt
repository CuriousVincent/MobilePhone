package com.vincentwang.mobilephone.ui.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vincentwang.mobilephone.R
import com.vincentwang.mobilephone.databinding.ItemCurrencyBinding
import com.vincentwang.mobilephone.model.data.CurrencyListData

class CurrencyAdapter : ListAdapter<CurrencyListData, BindingHolder>(CurrencyDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return BindingHolder(view)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        holder.binding?.apply {
            when(this){
                is ItemCurrencyBinding->{
                    vm = CurrencyItemViewModel(getItem(position))
                }
            }
            executePendingBindings()
        }
    }
}


class CurrencyDiffCallback : DiffUtil.ItemCallback<CurrencyListData>() {
    override fun areItemsTheSame(oldItem: CurrencyListData, newItem: CurrencyListData): Boolean {
        return oldItem.currency == newItem.currency
    }

    override fun areContentsTheSame(oldItem: CurrencyListData, newItem: CurrencyListData): Boolean {
        return oldItem == newItem
    }
}

class BindingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: ViewDataBinding? = DataBindingUtil.bind(itemView)
}