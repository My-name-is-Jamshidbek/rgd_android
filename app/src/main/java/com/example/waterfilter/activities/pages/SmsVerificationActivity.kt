package com.example.waterfilter.activities.pages

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waterfilter.R
import com.example.waterfilter.activities.MainActivity
import com.example.waterfilter.api.ApiClient
import com.example.waterfilter.api.ApiService
import com.example.waterfilter.data.common.JsonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SmsVerificationActivity : AppCompatActivity() {

    private lateinit var tvPhoneNumber: TextView
    private lateinit var tvCountdown: TextView
    private lateinit var btnContinue: Button
    private lateinit var btnResendSms: Button
    private lateinit var editTexts: Array<EditText>
    private lateinit var apiService: ApiService
    private lateinit var taskId: String
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_sms_verification)

        taskId = intent.getStringExtra("TASK_ID") ?: ""
        phoneNumber = intent.getStringExtra("PHONE") ?: ""

        tvPhoneNumber = findViewById(R.id.tv_phone_number)
        tvCountdown = findViewById(R.id.tv_countdown)
        btnContinue = findViewById(R.id.btn_continue)
        btnResendSms = findViewById(R.id.btn_resend_sms)
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
        startCountdown()

        btnContinue.setOnClickListener {
            val smsCode = editTexts.joinToString("") { it.text.toString() }
            if (smsCode.length == 6) {
                verifySmsCode(taskId, smsCode)
            } else {
                Toast.makeText(this, "Please enter a valid 6-digit code", Toast.LENGTH_SHORT).show()
            }
        }

        btnResendSms.setOnClickListener {
            // Implement the logic to resend the SMS
            resendSmsCode()
            startCountdown() // Restart the countdown after resending the SMS
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

    private fun startCountdown() {
        btnResendSms.visibility = Button.GONE
        tvCountdown.visibility = TextView.VISIBLE

        object : CountDownTimer(3 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                tvCountdown.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvCountdown.visibility = TextView.GONE
                btnResendSms.visibility = Button.VISIBLE
            }
        }.start()
    }

    private fun verifySmsCode(taskId: String, code: String) {
        val request = mapOf("code" to code.toInt())
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: return
        apiService.verifySmsCode("Bearer $token", taskId, request).enqueue(object : Callback<JsonResponse> {
            override fun onResponse(call: Call<JsonResponse>, response: Response<JsonResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(this@SmsVerificationActivity, "Verification successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@SmsVerificationActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SmsVerificationActivity, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                Toast.makeText(this@SmsVerificationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun resendSmsCode() {
        // Implement the logic to resend the SMS code
        Toast.makeText(this, "SMS resent", Toast.LENGTH_SHORT).show()
    }
}
