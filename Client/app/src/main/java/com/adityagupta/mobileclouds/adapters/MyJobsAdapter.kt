package com.adityagupta.mobileclouds.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.adityagupta.mobileclouds.R
import com.adityagupta.mobileclouds.model.MyJob
import com.adityagupta.mobileclouds.utils.ActionHelper
import com.adityagupta.mobileclouds.utils.NotificationHelper
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class MyJobsAdapter(private val items: List<MyJob>, private val context: Context) : RecyclerView.Adapter<MyJobsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobsViewHolder {
        return MyJobsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_my_jobs, parent, false))
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(holder: MyJobsViewHolder, position: Int) {
        val job = items[position]
        holder.fileView.text = job.file
        holder.ipView.text = job.savedAt.joinToString(",")

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Download File")
            builder.setMessage("Do you want to download file from ip list?")

            builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                Toast.makeText(context,
                    "Downloading File", Toast.LENGTH_SHORT).show()

                val storedFile = File(context.getExternalFilesDir(null), job.file.toString())
                FirebaseStorage.getInstance().reference.child("jobs").child(job.file.toString())
                    .getFile(storedFile)
                    .addOnSuccessListener {
                        NotificationHelper(context).createNotification(
                            "Open File",
                            "File Downloaded : ${job.file}",
                            ActionHelper.getFileIntent(context, storedFile)
                        )
                    }
            }

            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                Toast.makeText(context,
                    "Action Cancelled", Toast.LENGTH_SHORT).show()
            }

            builder.show()
        }
    }
}

class MyJobsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val fileView: AppCompatTextView = itemView.findViewById(R.id.fileName)
    val ipView: AppCompatTextView = itemView.findViewById(R.id.ipAddress)
}
