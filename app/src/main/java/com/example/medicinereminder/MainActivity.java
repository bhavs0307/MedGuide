package com.example.medicinereminder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medicinereminder.view.LoginActivity;
import com.example.medicinereminder.view.PharmacyLogin;
import com.example.medicinereminder.view.PharmacyRegistration;
import com.example.medicinereminder.view.RegisterActivity;

public class MainActivity extends AppCompatActivity {

    Button b1, b2, b3, b4;

    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET,
            Manifest.permission.POST_NOTIFICATIONS // For Android 13+
    };

    private int permissionIndex = 0;

    private ActivityResultLauncher<Intent> settingsLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (permissionsGranted()) {
                    navigateToLogin();
                } else {
                    Toast.makeText(this, "Permissions are still required!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        b1 = findViewById(R.id.userloginButton);
        b2 = findViewById(R.id.userregisterButton);
        b3 = findViewById(R.id.pharmacyregisterButton);
        b4 = findViewById(R.id.pharmacyloginButton);

        // Request all permissions on app start
        requestNextPermission();

        // Set click listeners for buttons
        b1.setOnClickListener(view -> {
            if (permissionsGranted()) {
                navigateToLogin();
            } else {
                Toast.makeText(this, "Please grant all permissions to proceed.", Toast.LENGTH_SHORT).show();
            }
        });

        b2.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        });

        b3.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), PharmacyRegistration.class);
            startActivity(i);
        });

        b4.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), PharmacyLogin.class);
            startActivity(i);
        });
    }

    private void requestNextPermission() {
        if (permissionIndex < REQUIRED_PERMISSIONS.length) {
            String permission = REQUIRED_PERMISSIONS[permissionIndex];

            // Check if the permission is for notifications (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    permission.equals(Manifest.permission.POST_NOTIFICATIONS)) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permission}, permissionIndex);
                } else {
                    permissionIndex++;
                    requestNextPermission();
                }
            } else {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{permission}, permissionIndex);
                } else {
                    permissionIndex++;
                    requestNextPermission();
                }
            }
        }
    }

    private boolean permissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void navigateToLogin() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode < REQUIRED_PERMISSIONS.length) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionIndex++;
                requestNextPermission();
            } else {
                handlePermissionDenied(REQUIRED_PERMISSIONS[requestCode]);
            }
        }
    }

    private void handlePermissionDenied(String permission) {
        boolean showRationale = shouldShowRequestPermissionRationale(permission);
        if (!showRationale) {
            openSettingsDialog();
        } else {
            Toast.makeText(this, "Permission is required to proceed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Required Permissions");
        builder.setMessage("This app requires permissions to use its features. Please grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            settingsLauncher.launch(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
