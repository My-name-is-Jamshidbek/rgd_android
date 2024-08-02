package com.example.waterfilter.utility

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.waterfilter.R
import java.util.Objects
import com.example.waterfilter.utility.WebCheckTask // Ensure this line is present

class NetworkChangeListener(private val callback: NetworkChangeCallback) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        @SuppressLint("StaticFieldLeak") val webCheckTask = object : WebCheckTask() {
            override fun onPostExecute(result: Boolean) {
                if (result) {
                    Toast.makeText(context, "Qurilmaga muvaffaqiyatli bog'lanildi.", Toast.LENGTH_SHORT).show()
                    // Call the callback when the network is connected
                    callback.onNetworkConnected()
                } else {
                    Toast.makeText(context, "Qurilmaga bog'lanishni iloji bo'lmadi.", Toast.LENGTH_SHORT).show()
                    val builder = AlertDialog.Builder(context)
                    val layoutDialog = LayoutInflater.from(context).inflate(R.layout.check_internet_dialog, null)
                    builder.setView(layoutDialog)

                    val btnRetry: AppCompatButton = layoutDialog.findViewById(R.id.btn_retry)

                    val dialog = builder.create()
                    dialog.show()
                    dialog.setCancelable(false)
                    Objects.requireNonNull(dialog.window)?.setGravity(Gravity.CENTER)

                    btnRetry.setOnClickListener {
                        dialog.dismiss()
                        onReceive(context, intent)
                    }
                }
            }
        }
        webCheckTask.execute("http://rgd.amusoft.uz/")
    }

    interface NetworkChangeCallback {
        fun onNetworkConnected()
    }
}
