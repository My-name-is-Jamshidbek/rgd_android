package com.example.waterfilter.fragments

import android.content.Intent
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
import com.example.waterfilter.activities.pages.LoginActivity
import com.example.waterfilter.adapters.AgentProductsListAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
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
                        if (products.isNotEmpty()) {
                            Toast.makeText(
                                context,
                                "Agent mahsulotlari soni: ${products.count()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val agentProductsListAdapter =
                                AgentProductsListAdapter(products)
                            recyclerView.adapter = agentProductsListAdapter
                        } else {
                            Toast.makeText(context, "Agent mahsulotlari aniqlanmadi", Toast.LENGTH_SHORT).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Mahsulotlar mavjud emas", Toast.LENGTH_SHORT).show()
                    }
                } else if (response.code() == 401) {
                    // Handle unauthorized access (401)
                    Toast.makeText(requireContext(), "Siz tizimga kirmagansiz. Iltimos, qayta kiring.", Toast.LENGTH_SHORT).show()

                    // Clear shared preferences or any stored user session data
                    val editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    // Redirect the user to the login activity
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the back stack
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AgentProductsListResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
