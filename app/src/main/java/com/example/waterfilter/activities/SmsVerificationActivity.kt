package com.example.waterfilter.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waterfilter.R
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.api.JsonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsVerificationActivity : AppCompatActivity() {

    private lateinit var tvPhoneNumber: TextView
    private lateinit var btnContinue: Button
    private lateinit var editTexts: Array<EditText>
    private lateinit var apiService: ApiService
    private lateinit var taskId: String
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_verification)

        taskId = intent.getStringExtra("TASK_ID") ?: ""
        phoneNumber = intent.getStringExtra("PHONE") ?: ""

        tvPhoneNumber = findViewById(R.id.tv_phone_number)
        btnContinue = findViewById(R.id.btn_continue)
        editTexts = arrayOf(
            findViewById(R.id.et_digit_1),
            findViewById(R.id.et_digit_2),
            findViewById(R.id.et_digit_3),
            findViewById(R.id.et_digit_4),
            findViewById(R.id.et_digit_5),
            findViewById(R.id.et_digit_6)
        )

        apiService = ApiClient.getApiService(this)

        tvPhoneNumber.text = phoneNumber

        setupEditTexts()

        btnContinue.setOnClickListener {
            val smsCode = editTexts.joinToString("") { it.text.toString() }
            if (smsCode.length == 6) {
                verifySmsCode(taskId, smsCode)
            } else {
                Toast.makeText(this, "Please enter a valid 6-digit code", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupEditTexts() {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        } else {
                            editTexts[i].clearFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun verifySmsCode(taskId: String, code: String) {
        val request = mapOf("code" to code.toInt())
        apiService.verifySmsCode(taskId, request).enqueue(object : Callback<JsonResponse> {
            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@SmsVerificationActivity, "Verification successful", Toast.LENGTH_SHORT).show()
                    // Handle successful verification
                } else {
                    Toast.makeText(this@SmsVerificationActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                Toast.makeText(this@SmsVerificationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
