package com.example.waterfilter.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.waterfilter.R
import com.example.waterfilter.activities.pages.LoginActivity
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.updateProfile.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var userNameEditText: EditText
    private lateinit var userPhoneEditText: EditText
    private lateinit var userOldPasswordEditText: EditText
    private lateinit var userNewPasswordText: EditText
    private lateinit var userNewPasswordConfirmText: EditText

    @SuppressLint("CutPasteId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "N/A")
        val userPhone = sharedPreferences.getString("userPhone", "N/A")

        userNameEditText = view.findViewById(R.id.editFullName)
        userPhoneEditText = view.findViewById(R.id.editPhone)
        userOldPasswordEditText = view.findViewById(R.id.oldPassword)
        userNewPasswordText = view.findViewById(R.id.editNewPassword)
        userNewPasswordConfirmText = view.findViewById(R.id.editNewPasswordConfirm)

        userNameEditText.setText(userName)
        userPhoneEditText.setText(userPhone)

        apiService = ApiClient.getApiService(requireContext())

        val updateButton: Button = view.findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            val newUserName = userNameEditText.text.toString()
            val newUserPhone = userPhoneEditText.text.toString()
            val oldPassword = userOldPasswordEditText.text.toString()
            val newPassword = userNewPasswordText.text.toString()
            val newPasswordConfirm = userNewPasswordConfirmText.text.toString()
            if (newPassword != newPasswordConfirm) {
                Toast.makeText(requireContext(), "Iltimos parolni to`g`ri kiriting!", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(newUserName, newUserPhone, oldPassword, newPassword, newPasswordConfirm)
                updateUserData(user, userOldPasswordEditText, userNewPasswordText, userNewPasswordConfirmText)
            }
        }

        return view
    }

    private fun updateUserData(user: User, userOldPasswordEditText: EditText, userNewPasswordText: EditText, userNewPasswordConfirmText: EditText) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "") ?: return@launch
                val response = apiService.updateUser("Bearer $token", user)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        // Save updated user information to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("userName", user.name)
                        editor.putString("userPhone", user.phone)
                        editor.apply()

                        Toast.makeText(requireContext(), "Foydalanuvchi muvaffaqiyatli yangilandi", Toast.LENGTH_SHORT).show()
                        // Clear password fields
                        userOldPasswordEditText.text.clear()
                        userNewPasswordText.text.clear()
                        userNewPasswordConfirmText.text.clear()
                    } else {
                        when (response.code()) {
                            401 -> {
                                Toast.makeText(requireContext(), "Siz tizimga kirmagansiz. Iltimos, qayta kiring.", Toast.LENGTH_SHORT).show()
                                val editor = sharedPreferences.edit()
                                editor.clear()
                                editor.apply()
                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            404 -> {
                                Toast.makeText(requireContext(), "Yangilash amalga oshmadi: Resurs topilmadi", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                val errorMessage = response.errorBody()?.string()
                                Toast.makeText(context, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
                                Toast.makeText(requireContext(), "Failed to update user: $errorMessage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Iltimos internet aloqasini tekshiring!", Toast.LENGTH_SHORT).show()
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
