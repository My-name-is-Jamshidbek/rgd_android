package com.example.waterfilter.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.waterfilter.R
import java.util.Calendar

class AgentHistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agent_history, container, false)

        // Find the button by its ID
        val buttonSelectDate: Button = view.findViewById(R.id.buttonSelectDate)

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
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    // Update the button text with the selected date
                    buttonSelectDate.text = selectedDate
                }, year, month, day)

            // Show the DatePickerDialog
            datePickerDialog.show()
        }

        return view
    }
}
