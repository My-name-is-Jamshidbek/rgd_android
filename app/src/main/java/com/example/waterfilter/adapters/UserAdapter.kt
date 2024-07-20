package com.example.waterfilter.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.data.User

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
//        holder.fullNameTextView.text = "F.I.O: "+user.fullName
//        holder.phoneTextView.text = "Telefon raqami: "+user.phone
//        holder.addressTextView.text = "Manzil: "+user.address

        holder.locationButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${user.latitude},${user.longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    }

    override fun getItemCount() = userList.size
}
