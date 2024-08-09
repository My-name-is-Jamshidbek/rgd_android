package com.example.waterfilter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.waterfilter.R
import com.example.waterfilter.adapters.AgentProductsListAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.common.AgentProduct
import com.example.waterfilter.data.getProducts.AgentProductsListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AgentProductsFragment : Fragment() {
    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agent_products, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)

        apiService = ApiClient.getApiService(requireContext())

        fetchProducts()

        swipeRefreshLayout.setOnRefreshListener {
            fetchProducts()
        }

        return view
    }

    private fun fetchProducts() {
        val sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return

        apiService.getAgentProducts("Bearer $token").enqueue(object : Callback<AgentProductsListResponse> {
            override fun onResponse(call: Call<AgentProductsListResponse>, response: Response<AgentProductsListResponse>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    response.body()?.products?.let { products ->
                        Toast.makeText(context, "Agent products count: ${products.count()}", Toast.LENGTH_SHORT).show()
                        val agentProductsListAdapter = AgentProductsListAdapter(requireContext(), products)
                        recyclerView.adapter = agentProductsListAdapter
                    } ?: run {
                        Toast.makeText(context, "No products available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AgentProductsListResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
