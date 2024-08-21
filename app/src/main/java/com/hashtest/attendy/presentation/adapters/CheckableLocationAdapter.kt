package com.hashtest.attendy.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hashtest.attendy.databinding.ItemLocationBinding
import com.hashtest.attendy.domain.models.LocationPlace

class CheckableLocationAdapter(private val listItem: MutableList<LocationPlace>): RecyclerView.Adapter<CheckableLocationAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemLocationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LocationPlace){
            binding.apply {
                tvLocationName.text = data.locationName
                tvLocationAddress.text = data.locationAddress


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listItem[position])
    }
}