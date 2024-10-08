package com.hashtest.attendy.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.utils.minutesToTime
import com.aglotest.algolist.utils.scaleViewOneShot
import com.hashtest.attendy.databinding.ItemLocationBinding
import com.hashtest.attendy.domain.models.LocationPlace

class CheckableLocationAdapter(private val listItem: MutableMap<LocationPlace, String>): RecyclerView.Adapter<CheckableLocationAdapter.ViewHolder>() {
    var onItemClick: ((String)->Unit)? = null
    fun submitData(locations: Map<LocationPlace, String>) {
        listItem.clear()
        listItem.putAll(locations)
        notifyItemRangeInserted(0, listItem.size)
    }

    inner class ViewHolder(private val binding: ItemLocationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LocationPlace){
            binding.apply {
                tvLocationName.text = data.locationName
                tvLocationAddress.text = data.locationAddress

                tvCheckInDue.text = minutesToTime(data.openTime)
                tvCheckOutStarts.text = minutesToTime(data.closeTime)

                root.setOnClickListener {
                    root.scaleViewOneShot(0.95f, 100)
                    onItemClick?.invoke(listItem[data]!!)
                }
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
        holder.bind(listItem.keys.elementAt(position))
    }
}