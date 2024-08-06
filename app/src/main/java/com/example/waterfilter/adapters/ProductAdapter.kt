package com.example.waterfilter.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.data.AgentProduct

class ProductAdapter(
    private val context: Context,
    private var taskProducts: MutableList<AgentProduct>,
    private val agentProducts: List<AgentProduct>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productSpinner: Spinner = view.findViewById(R.id.productSpinner)
        val productQuantity: TextView = view.findViewById(R.id.productQuantity)
        val removeProductButton: Button = view.findViewById(R.id.removeProductButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = taskProducts[position]

        // Create a list of product names for the Spinner
        val productNames = agentProducts.map { it.product.name }

        // Set up the Spinner with product names
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, productNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        holder.productSpinner.adapter = adapter

        // Set the Spinner selection to the current product's position
        holder.productSpinner.setSelection(productNames.indexOf(product.product.name).takeIf { it != -1 } ?: 0)

        // Set initial product quantity
        holder.productQuantity.text = "${product.quantity} so'm"

        // Set an item selected listener on the Spinner
        holder.productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected product
                val selectedProduct = agentProducts[position]

                // Update the product quantity TextView
                holder.productQuantity.text = "${selectedProduct.quantity} so'm"
                taskProducts[holder.adapterPosition] = selectedProduct // Update the taskProducts list
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        // Set remove button click listener
        holder.removeProductButton.setOnClickListener {
            removeProduct(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = taskProducts.size

    @SuppressLint("NotifyDataSetChanged")
    fun addProduct() {
        taskProducts.add(agentProducts[0])
        notifyItemInserted(taskProducts.size + 1)
        notifyItemRangeChanged(0, taskProducts.size)
        notifyDataSetChanged()
        Log.d("ProductAdapter", "Product added: ${agentProducts[0].product.name}")
        Log.d("ProductAdapter", "Products: $taskProducts")
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeProduct(position: Int) {
        taskProducts.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, taskProducts.size - position) // Update this line
        notifyDataSetChanged()
        Log.d("ProductAdapter", "Product removed at position: $position, new size: ${taskProducts.size}")
    }
}
