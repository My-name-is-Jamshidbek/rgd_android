package com.example.waterfilter.adapters

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
import com.example.waterfilter.data.User
import com.google.android.material.bottomsheet.BottomSheetDialog

class UserAdapter(private val context: Context, private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullNameTextView: TextView = itemView.findViewById(R.id.fullNameTextView)
        val phoneTextView: TextView = itemView.findViewById(R.id.phoneTextView)
        val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)
        val locationButton: Button = itemView.findViewById(R.id.locationButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.fullNameTextView.text = user.fullName
        holder.phoneTextView.text = user.phone
        holder.addressTextView.text = user.address

        holder.locationButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${user.latitude},${user.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }

        holder.itemView.setOnClickListener {
            showBottomSheet(user)
        }
    }

    override fun getItemCount() = userList.size

    private fun showBottomSheet(user: User) {
        val bottomSheetDialog = BottomSheetDialog(context)
        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.user_item_bottom_sheet, null)

        val fullNameTextView = bottomSheetView.findViewById<TextView>(R.id.fullNameTextView)
        val phoneTextView = bottomSheetView.findViewById<TextView>(R.id.phoneTextView)
        val addressTextView = bottomSheetView.findViewById<TextView>(R.id.addressTextView)
        val locationButton = bottomSheetView.findViewById<Button>(R.id.locationButton)
        val serviceButton = bottomSheetView.findViewById<Button>(R.id.service)
        val callButton = bottomSheetView.findViewById<ImageButton>(R.id.callButton)

        fullNameTextView.text = user.fullName
        phoneTextView.text = user.phone
        addressTextView.text = user.address

        locationButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${user.latitude},${user.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }

        callButton.setOnClickListener {
            val callIntent = Intent(Intent.ACTION_DIAL)
            callIntent.data = Uri.parse("tel:${user.phone}")
            context.startActivity(callIntent)
        }

        // Add any additional button click logic for serviceButton

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }
}
