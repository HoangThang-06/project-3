package com.example.project_3.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.project_3.R
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class EventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventTitle = intent.getStringExtra("event_title") ?: "Sự kiện thú cưng"
        val currentTime = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault()).format(Date())

        // 1. LƯU THÔNG BÁO VÀO BỘ NHỚ TRONG CỦA APP (Để nhấn vào Chuông có dữ liệu)
        val sharedPrefs = context.getSharedPreferences("app_notifications", Context.MODE_PRIVATE)
        val savedNotificationsStr = sharedPrefs.getString("list", "[]")

        try {
            val jsonArray = JSONArray(savedNotificationsStr)
            val newNotification = JSONObject().apply {
                put("title", "Nhắc nhở sự kiện: $eventTitle")
                put("time", currentTime)
                put("isRead", false)
            }
            // Đẩy thông báo mới lên đầu danh sách
            val newArray = JSONArray()
            newArray.put(newNotification)
            for (i in 0 until jsonArray.length()) {
                newArray.put(jsonArray.get(i))
            }
            sharedPrefs.edit().putString("list", newArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2. PHÁT THÔNG BÁO RA ĐIỆN THOẠI (Giữ nguyên phần thông báo hệ thống)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "event_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Thông báo sự kiện", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_paw_print)
            .setContentTitle("Nhắc nhở sự kiện sắp diễn ra!")
            .setContentText("Hôm nay có sự kiện: $eventTitle. Đừng bỏ lỡ nhé!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}