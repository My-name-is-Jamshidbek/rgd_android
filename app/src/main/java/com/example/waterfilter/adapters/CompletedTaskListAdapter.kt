package com.example.waterfilter.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.activities.pages.TaskActivity
import com.example.waterfilter.data.common.Task
import com.google.android.material.bottomsheet.BottomSheetDialog

class CompletedTaskListAdapter(private val context: Context, private val taskList: List<Task>) : RecyclerView.Adapter<CompletedTaskListAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val locationButton: Button = itemView.findViewById(R.id.locationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = taskList[position]
        holder.fullNameTextView.text = currentTask.client.name
        holder.phoneTextView.text = currentTask.client.phone
        holder.addressTextView.text = currentTask.point.address

        holder.locationButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${currentTask.point.latitude},${currentTask.point.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }

        holder.itemView.setOnClickListener {
            showBottomSheet(currentTask)
        }
    }

    override fun getItemCount() = taskList.size

    @SuppressLint("InflateParams")
    private fun showBottomSheet(task: Task) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_completed_bottom_sheet, null)

        val fullNameTextView = bottomSheetView.findViewById<TextView>(R.id.fullNameTextView)
        val phoneTextView = bottomSheetView.findViewById<TextView>(R.id.phoneTextView)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.addressTextView)
        val locationButton = bottomSheetView.findViewById<Button>(R.id.locationButton)
        val callButton = bottomSheetView.findViewById<ImageButton>(R.id.callButton)

        fullNameTextView.text = task.client.name
        phoneTextView.text = task.client.phone
        addressTextView.text = task.point.address

        locationButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${task.point.latitude},${task.point.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }

        callButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${task.client.phone}")
            context.startActivity(callIntent)
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}
