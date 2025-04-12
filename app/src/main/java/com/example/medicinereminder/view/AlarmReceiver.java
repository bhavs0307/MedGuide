package com.example.medicinereminder.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve medicine details from the intent extras
        String medicineId = intent.getStringExtra("medicineid");

        // Show notification with the medicine details
        showNotification(context, medicineId);
    }

    private void showNotification(Context context, String medicineId) {
        int notificationId = 1;
        String channelId = "alarm_channel";

        // Create a NotificationChannel if it doesn't exist (required for API 26+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            // Check if the channel exists, if it does, delete and recreate it
            NotificationChannel existingChannel = notificationManager.getNotificationChannel(channelId);
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel(channelId);  // Delete the existing channel to recreate it properly
            }

            // Now recreate the channel with proper sound settings
            CharSequence channelName = "Medicine Alarm Channel";
            String channelDescription = "Channel for Medicine Alarm Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);
            channel.enableLights(true);  // Enable notification lights if needed
            channel.enableVibration(true);  // Enable vibration if needed

            // Set the sound for the notification channel
            channel.setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI,
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)  // Ensure it's treated as an alarm
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build());

            // Register the channel with the notification manager
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent to open ViewNotification activity when notification is clicked
        Intent activityIntent = new Intent(context, ViewNotification.class);
        activityIntent.putExtra("medicineid", medicineId);
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent to trigger when the notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification with sound
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medicine Reminder")
                .setContentText("It's time to take your medicine.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure high priority for sound and popup
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)  // Default alarm sound
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);  // Set default sound, vibration, lights

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request notification permission if needed
            return;
        }
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}