package com.hashtest.attendy.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aglotest.algolist.utils.convertTimeMillisToTimeFormat
import com.hashtest.attendy.databinding.ItemAttendanceHistoryBinding
import com.hashtest.attendy.domain.models.Attendance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AttendanceListAdapter(private val listItem: MutableList<Attendance>):RecyclerView.Adapter<AttendanceListAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemAttendanceHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Attendance){
            binding.apply {
                tvPunchIn.text = convertTimeMillisToTimeFormat(data.checkInTime)
                tvPunchOut.text = if(data.checkOutTime != null){
                    convertTimeMillisToTimeFormat(data.checkOutTime)
                }else{
                    "--:--"
                }

                val date = Date(data.checkInTime)
                val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
                val dayFormat = SimpleDateFormat("dd", Locale.getDefault())

                tvDay.text = dayFormat.format(date)
                tvMonth.text = monthFormat.format(date)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemAttendanceHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listItem[position])
    }
}