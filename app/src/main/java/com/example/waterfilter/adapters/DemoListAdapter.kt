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
import com.example.waterfilter.activities.pages.DemoActivity
import com.example.waterfilter.data.common.Demo
import com.example.waterfilter.data.common.Point
import com.google.android.material.bottomsheet.BottomSheetDialog

class DemoListAdapter(private val context: Context, private val demoList: List<Point>) : RecyclerView.Adapter<DemoListAdapter.demoViewHolder>() {

    inner class demoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val descTextView: TextView = itemView.findViewById(R.id.descTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): demoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_demo, parent, false)
        return demoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: demoViewHolder, position: Int) {
        val currentdemo = demoList[position]
        holder.fullNameTextView.text = currentdemo.client.name
        holder.phoneTextView.text = currentdemo.client.phone
        holder.addressTextView.text = currentdemo.address
        holder.descTextView.text = currentdemo.client.description

        holder.itemView.setOnClickListener {
            showBottomSheet(currentdemo)
        }
    }

    override fun getItemCount() = demoList.size

    @SuppressLint("InflateParams", "MissingInflatedId")
    private fun showBottomSheet(demo: Point) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.item_user_bottom_sheet, null)

        val fullNameTextView = bottomSheetView.findViewById<TextView>(R.id.fullNameTextView)
        val phoneTextView = bottomSheetView.findViewById<TextView>(R.id.phoneTextView)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.addressTextView)
        val callButton = bottomSheetView.findViewById<ImageButton>(R.id.callButton)
        val serviceButton = bottomSheetView.findViewById<ImageButton>(R.id.serviceButton)

        fullNameTextView.text = demo.client.name
        phoneTextView.text = demo.client.phone
        addressTextView.text = demo.address

        callButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${demo.client.phone}")
            context.startActivity(callIntent)
        }

        // Updated serviceButton click listener
        serviceButton.setOnClickListener {
            val intent = Intent(context, DemoActivity::class.java)
            intent.putExtra("DEMO_ID", demo.id.toString())
            context.startActivity(intent)
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}
