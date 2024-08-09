package com.example.waterfilter.adapters

import android.annotation.SuppressLint
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
        val productTypeTextView: TextView = itemView.findViewById(R.id.productTypeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgentProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_products, parent, false)
        return AgentProductsViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AgentProductsViewHolder, position: Int) {
        val currentProduct = productList[position]
        holder.productNameTextView.text = "${currentProduct.product.name} ${currentProduct.quantity} ta"
        holder.productPriceTextView.text = "${currentProduct.price}/${currentProduct.servicePrice}"
        holder.productTypeTextView.text = if (currentProduct.product.type == 0) "Filter" else "Mahsulot"
    }

    override fun getItemCount() = productList.size
}
