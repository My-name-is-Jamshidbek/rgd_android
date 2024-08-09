package com.example.waterfilter.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.data.common.AgentProduct

class AgentProductsListAdapter(private val context: Context, private val productList: List<AgentProduct>) : RecyclerView.Adapter<AgentProductsListAdapter.AgentProductsViewHolder>() {

    inner class AgentProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return AgentProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AgentProductsViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.productNameTextView.text = currentProduct.product.name
        holder.productPriceTextView.text = currentProduct.price.toString()
    }

    override fun getItemCount() = productList.size
}
