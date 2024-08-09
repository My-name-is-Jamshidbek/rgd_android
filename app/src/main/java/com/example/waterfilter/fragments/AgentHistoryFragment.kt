package com.example.waterfilter.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.waterfilter.R
import com.example.waterfilter.adapters.CompletedTaskListAdapter
import com.example.waterfilter.adapters.TaskListAdapter
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.completedTasks.CompletedTasksRequest
import com.example.waterfilter.data.getTasks.TaskListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgentHistoryFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var selectedDate: Date
    private lateinit var dateFormat: SimpleDateFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agent_history, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        apiService = ApiClient.getApiService(requireContext())

        // Initialize date formatter
        dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Initialize selectedDate with the current date
        selectedDate = Calendar.getInstance().time

        // Find the button by its ID
        val buttonSelectDate: Button = view.findViewById(R.id.buttonSelectDate)

        // Set the initial button text to the current date
        buttonSelectDate.text = dateFormat.format(selectedDate)

        // Set an OnClickListener to show the DatePickerDialog when the button is clicked
        buttonSelectDate.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create a DatePickerDialog
            val datePickerDialog = DatePickerDialog(requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Handle the date chosen by the user
                    val calendarSelected = Calendar.getInstance()
                    calendarSelected.set(selectedYear, selectedMonth, selectedDay)
                    selectedDate = calendarSelected.time
                    // Update the button text with the selected date
                    buttonSelectDate.text = dateFormat.format(selectedDate)

                    // Fetch tasks for the newly selected date
                    fetchTasks(selectedDate)
                }, year, month, day)

            // Show the DatePickerDialog
            datePickerDialog.show()
        }

        fetchTasks(selectedDate)
        Toast.makeText(context, dateFormat.format(selectedDate).toString(), Toast.LENGTH_SHORT).show()

        swipeRefreshLayout.setOnRefreshListener {
            fetchTasks(selectedDate)
        }

        return view
    }

    private fun fetchTasks(date: Date) {
        val sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return
        val request = CompletedTasksRequest(completed_time = dateFormat.format(selectedDate).toString())

        apiService.getCompletedTasks("Bearer $token", request).enqueue(object : Callback<TaskListResponse> {
            override fun onResponse(call: Call<TaskListResponse>, response: Response<TaskListResponse>) {
                swipeRefreshLayout.isRefreshing = false
                if (response.isSuccessful) {
                    response.body()?.tasks?.let { tasks ->
                        Toast.makeText(context, "Completed tasks count ${tasks.count()}", Toast.LENGTH_SHORT).show()
                        val taskListAdapter = CompletedTaskListAdapter(requireContext(), tasks)
                        recyclerView.adapter = taskListAdapter
                    } ?: run {
                        Toast.makeText(context, "No tasks available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskListResponse>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
