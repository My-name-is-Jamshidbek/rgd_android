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
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterfilter.R
import com.example.waterfilter.activities.pages.TaskActivity
import com.example.waterfilter.data.common.AgentProduct

class ProductAdapter(
    private val context: Context,
    private var taskProducts: MutableList<AgentProduct>,
    private val agentProducts: List<AgentProduct>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productSpinner: Spinner = view.findViewById(R.id.productSpinner)
        val productQuantity: TextView = view.findViewById(R.id.productQuantity)
        val removeProductButton: Button = view.findViewById(R.id.removeProductButton)
        val checkBox: CheckBox = view.findViewById(R.id.productCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val product = taskProducts[position]

        // Set the checkbox state based on the product's isSelected property
        holder.checkBox.setOnCheckedChangeListener(null)  // Clear any previous listener
        holder.checkBox.isChecked = product.isSelected

        // Set a new listener for the checkbox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                taskProducts[adapterPosition].isSelected = isChecked
            }
        }


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
        holder.productQuantity.text = "${product.price} so'm"

        // Set an item selected listener on the Spinner
        holder.productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, spinnerPosition: Int, id: Long) {
                // Get the selected product
                val selectedProduct = agentProducts[spinnerPosition]

                // Update the product quantity TextView
                holder.productQuantity.text = "${selectedProduct.price} so'm"
                val newItem = selectedProduct.copy() // Update the taskProducts list
                newItem.isSelected =taskProducts[holder.adapterPosition].isSelected
                taskProducts[holder.adapterPosition] = newItem

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
        // Create a new copy of the product
        val item = agentProducts[0].copy()


        // Add the new instance to the taskProducts list
        Log.d("ProductAdapter", "Products size: ${taskProducts.size}")
        taskProducts.add(item)
        Log.d("ProductAdapter", "Products size: ${taskProducts.size}")
        Log.d("ProductAdapter", "Product selected: ${item.isSelected}")

        // Notify the adapter about the new item inserted\
        ;
        if(taskProducts.size == 1){
            notifyDataSetChanged()

        } else {
            notifyItemInserted(taskProducts.size - 1)
//            notifyItemRangeChanged(taskProducts.size - 1, 1)
//            (context as TaskActivity).findViewById<NestedScrollView>(R.id.scrollView).requestLayout()
            (context as TaskActivity).findViewById<RecyclerView>(R.id.productRecyclerView).smoothScrollToPosition(taskProducts.size - 1)
        }

        Log.d("ProductAdapter", "notifyItemInserted called for position: ${taskProducts.size - 1}")
    }


    fun removeProduct(position: Int) {
        Log.d("ProductAdapter", "Position: $position, Size: ${taskProducts.size}")
        if (position != RecyclerView.NO_POSITION) {
            taskProducts.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(
                position,
                taskProducts.size - position - 1
            ) // Notify that range of items have changed
        }
        Log.d("ProductAdapter", "Position: $position, Size: ${taskProducts.size}")

    }
}
