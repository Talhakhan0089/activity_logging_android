package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(private val activities: List<Activity>) :
    RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val activity = activities[position]
        holder.bind(activity)
    }

    override fun getItemCount() = activities.size

    class ActivityViewHolder(private val binding: ItemActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: Activity) {
            binding.textViewActivityName.text = activity.name
            binding.textViewUser.text = activity.user
            binding.textViewNotes.text = activity.notes

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val startTime = timeFormat.format(Date(activity.startTime))
            val endTime = timeFormat.format(Date(activity.endTime))

            binding.textViewStartTime.text = "Start: $startTime"
            binding.textViewEndTime.text = "End: $endTime"
        }
    }
}
