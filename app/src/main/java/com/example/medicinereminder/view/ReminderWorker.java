package com.example.medicinereminder.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.medicinereminder.R;
import com.example.medicinereminder.util.Constants;

public class ReminderWorker extends Worker {

    public ReminderWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        // Get the medicine ID passed in input data
        String medicineId = getInputData().getString("medicineid");

        // Show the reminder notification
        showNotification(medicineId);

        // Return success after showing the notification
        return Result.success();
    }

    private void showNotification(String medicineId) {
        String channelId = "medicine_reminder_channel";
        String channelName = "Medicine Reminder Notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the notification channel (for Android 8.0 and higher)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent to open ViewNotification
        Intent intent = new Intent(getApplicationContext(), ViewNotification.class);
        intent.putExtra("medicineid", medicineId); // Pass medicine ID to the activity
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Create the notification
        Notification notification = new Notification.Builder(getApplicationContext(), channelId)
                .setContentTitle("Medicine Reminder")
                .setContentText("It's time to take your medicine!")
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your own icon
                .setContentIntent(pendingIntent) // Attach the PendingIntent to the notification
                .setAutoCancel(true) // Dismiss the notification when clicked
                .build();

        // Show the notification
        notificationManager.notify(1, notification);  // 1 is the notification ID
    }
}
