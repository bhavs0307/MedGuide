package com.example.medicinereminder.view;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.medicinereminder.R;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AddMedicine extends AppCompatActivity {

    private EditText etName, etStockCount, etUsage;
    private ChipGroup chipGroup;
    private Switch isRepeat;
    private ProgressBar progressBar;
    private Button btnSetAlarm, btnSelectTime, chooseImageButton, takePictureButton;
    private ImageView postMedicineImgView;
    private String alarmTime = "";  // Initialize alarmTime as empty
    private Uri imageUri;

    // Launchers for Activity Result API
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        postMedicineImgView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    postMedicineImgView.setImageBitmap(imageBitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        etName = findViewById(R.id.addName);
        etStockCount = findViewById(R.id.addStockCount);
        etUsage = findViewById(R.id.addUsage);
        isRepeat = findViewById(R.id.repeat_switch);
        progressBar = findViewById(R.id.progressBar);
        btnSetAlarm = findViewById(R.id.btnAdd);
        btnSelectTime = findViewById(R.id.add_med_time);
        chooseImageButton = findViewById(R.id.chooseimagebutton);
        takePictureButton = findViewById(R.id.takepicturebutton);
        postMedicineImgView = findViewById(R.id.postMedicineImgView);

        // Time Picker Button Click Listener
        btnSelectTime.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            Bundle args = new Bundle();
            args.putString("alarmTime", alarmTime);  // Pass alarmTime to the fragment
            timePicker.setArguments(args);
            timePicker.show(getSupportFragmentManager(), "time picker");
        });

        // Alarm Button Click Listener
        btnSetAlarm.setOnClickListener(v -> {
            String medicineId = etName.getText().toString();
            String stockCount = etStockCount.getText().toString();
            String usage = etUsage.getText().toString();

            if (TextUtils.isEmpty(medicineId)) {
                Toast.makeText(AddMedicine.this, "Please enter a medicine name", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(stockCount)) {
                Toast.makeText(AddMedicine.this, "Please enter stock count", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(usage)) {
                Toast.makeText(AddMedicine.this, "Please enter usage instructions", Toast.LENGTH_SHORT).show();
            } else {
                setAlarm(medicineId, stockCount, usage);
            }
        });

        // Image selection buttons
        chooseImageButton.setOnClickListener(v -> chooseImage());
        takePictureButton.setOnClickListener(v -> takePicture());
        isRepeat.setOnClickListener(v -> setChildrenEnabled(chipGroup, isRepeat.isChecked()));
    }

    public static class TimePickerFragment extends DialogFragment {

        private String alarmTime;

        @NonNull
        @Override
        public TimePickerDialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            if (getArguments() != null) {
                alarmTime = getArguments().getString("alarmTime", "00:00");
            }

            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> {
                alarmTime = String.format("%02d:%02d", hourOfDay, minute);
                Toast.makeText(getActivity(), "Alarm time set to: " + alarmTime, Toast.LENGTH_SHORT).show();

                AddMedicine activity = (AddMedicine) getActivity();
                if (activity != null) {
                    activity.alarmTime = alarmTime;
                }
            }, hour, min, DateFormat.is24HourFormat(getActivity()));
        }
    }

    public void setAlarm(String medicineId, String stockCount, String usage) {
        if (TextUtils.isEmpty(alarmTime)) {
            Toast.makeText(this, "Please set a valid alarm time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        String[] timeParts = alarmTime.split(":");
        if (timeParts.length == 2) {
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            scheduleReminderWithWorkManager(medicineId, stockCount, usage, calendar.getTimeInMillis());
        } else {
            Toast.makeText(this, "Invalid time format.", Toast.LENGTH_SHORT).show();
        }
    }

    public void scheduleReminderWithWorkManager(String medicineId, String stockCount, String usage, long triggerTime) {
        WorkRequest reminderRequest = new
                OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(triggerTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addTag(medicineId)
                .build();

        WorkManager.getInstance(this).enqueue(reminderRequest);

        Log.d("AddMedicine", "Reminder set for: " + medicineId + ", Stock: " + stockCount + ", Usage: " + usage);
        Toast.makeText(this, "Reminder set for: " + medicineId, Toast.LENGTH_SHORT).show();
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureLauncher.launch(intent);
    }

    private void setChildrenEnabled(ChipGroup chipGroup, boolean enable) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            chipGroup.getChildAt(i).setEnabled(enable);
        }
    }
}
