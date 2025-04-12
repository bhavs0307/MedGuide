package com.example.medicinereminder.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.medicinereminder.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Device rebooted. Re-scheduling alarms...");
            rescheduleAlarms(context);
        }
    }

    private void rescheduleAlarms(Context context) {
        DatabaseReference medicineRef = FirebaseDatabase.getInstance().getReference(Constants.MEDICINE_DB);

        medicineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot medicineSnapshot : snapshot.getChildren()) {
                    try {
                        // Retrieve medicine data
                        String medicineName = medicineSnapshot.child("name").getValue(String.class);
                        Long hour = medicineSnapshot.child("hour").getValue(Long.class);
                        Long minute = medicineSnapshot.child("minute").getValue(Long.class);

                        if (medicineName != null && hour != null && minute != null) {
                            scheduleAlarm(context, medicineName, hour.intValue(), minute.intValue());
                        }
                    } catch (Exception e) {
                        Log.e("BootReceiver", "Error parsing medicine data", e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BootReceiver", "Failed to retrieve medicine data from Firebase", error.toException());
            }
        });
    }

    private void scheduleAlarm(Context context, String medicineName, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                medicineName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("BootReceiver", "Alarm scheduled for " + medicineName + " at " + hour + ":" + minute);
        } else {
            Log.e("BootReceiver", "AlarmManager is null");
        }
    }
}
