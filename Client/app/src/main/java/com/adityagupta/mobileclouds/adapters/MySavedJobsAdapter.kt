package com.adityagupta.mobileclouds.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.adityagupta.mobileclouds.R
import com.adityagupta.mobileclouds.model.SavedJob

class MySavedJobsAdapter(private val items: List<SavedJob>, private val context: Context) : RecyclerView.Adapter<MySavedJobsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySavedJobsViewHolder {
        return MySavedJobsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_saved_jobs, parent, false))
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: MySavedJobsViewHolder, position: Int) {
        val job = items[position]
        holder.fileView.text = job.user
        holder.ipView.text = job.totalJobs.toString()
    }
}

class MySavedJobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fileView: AppCompatTextView = itemView.findViewById(R.id.fileName)
    val ipView: AppCompatTextView = itemView.findViewById(R.id.ipAddress)
}

