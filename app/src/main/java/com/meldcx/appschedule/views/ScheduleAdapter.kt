package com.meldcx.appschedule.views

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meldcx.appschedule.R
import com.meldcx.appschedule.database.AppSchedule
import com.meldcx.appschedule.dateutils.DateTimeUtils

class ScheduleAdapter(
    private var schedules: List<AppSchedule>,
    private val onItemEditClick: (AppSchedule) -> Unit,
    private val onDeleteClick: (AppSchedule) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appNameTextView: TextView = itemView.findViewById(R.id.tvAppName)
        val packageNameTextView: TextView = itemView.findViewById(R.id.tvPackageName)
        val launchTimeTextView: TextView = itemView.findViewById(R.id.tvScheduleTime)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
        val editButton: Button = itemView.findViewById(R.id.btnEdit)

        fun bind(schedule: AppSchedule) {
            appNameTextView.text = schedule.appName
            packageNameTextView.text = schedule.packageName
            launchTimeTextView.text = DateTimeUtils.convertMillisToDateTime(schedule.launchTime)

            itemView.setOnClickListener {
                onItemEditClick(schedule)
            }
            deleteButton.setOnClickListener {
                onDeleteClick(schedule)
            }
            editButton.setOnClickListener {
                onItemEditClick(schedule)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        holder.bind(schedules[position])
    }

    override fun getItemCount(): Int = schedules.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newSchedules: List<AppSchedule>) {
        schedules = newSchedules
        notifyDataSetChanged()
    }
}