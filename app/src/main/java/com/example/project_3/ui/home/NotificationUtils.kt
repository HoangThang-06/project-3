package com.example.project_3.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

fun scheduleEventNotification(context: Context, eventTitle: String, eventDateStr: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 1. KIỂM TRA QUYỀN ĐẶT LỊCH CHÍNH XÁC (Chỉ áp dụng từ Android 12 / API 31 trở lên)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                "Vui lòng cấp quyền 'Báo thức & nhắc nhở' để nhận thông báo sự kiện!",
                Toast.LENGTH_LONG
            ).show()

            // Tự động mở màn hình Cài đặt hệ thống để người dùng bật nút cho phép
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.fromParts("package", context.packageName, null)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            return // Dừng hàm lại, chờ người dùng cấp quyền xong mới đặt lịch được
        }
    }

    try {
        // Định dạng ngày tháng từ database
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(eventDateStr) ?: return

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(context, "Sự kiện này diễn ra hôm nay hoặc đã qua!", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(context, EventReceiver::class.java).apply {
            putExtra("event_title", eventTitle)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventTitle.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. ĐẶT BÁO THỨC TRONG KHỐI TRY-CATCH ĐỂ ĐỀ PHÒNG LỖI BẢO MẬT (SecurityException)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(context, "Đã đặt lịch nhắc nhở cho: $eventTitle", Toast.LENGTH_SHORT).show()

    } catch (e: SecurityException) {
        // Xử lý an toàn nếu hệ thống từ chối quyền vào phút chót mà không làm sập App
        e.printStackTrace()
        Toast.makeText(context, "Lỗi bảo mật: Không thể đặt lịch do thiếu quyền hệ thống!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Không thể đặt lịch: Lỗi định dạng ngày!", Toast.LENGTH_SHORT).show()
    }
}