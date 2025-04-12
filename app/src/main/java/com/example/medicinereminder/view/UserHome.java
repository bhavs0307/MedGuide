package com.example.medicinereminder.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.R;
import com.example.medicinereminder.util.Session;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserHome extends AppCompatActivity {

    Button viewMedicine;
    Button userlogout;
    Button addMedicine;

    Button searchdrugs;
    Button vieworders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        viewMedicine=(Button) findViewById(R.id.viewMedicine);
        userlogout=(Button) findViewById(R.id.userlogout);
        addMedicine=(Button) findViewById(R.id.addMedicine);

        searchdrugs=(Button)findViewById(R.id.usersearchdrugs);
        vieworders=(Button)findViewById(R.id.usersvieworders);

        final Session s = new Session(getApplicationContext());

        addMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), AddMedicine.class);
                startActivity(i);
            }
        });

        viewMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListMedicines.class);
                startActivity(i);
            }
        });

        searchdrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),SearchPharmacy.class);
                startActivity(i);
            }
        });

        vieworders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),ListOrders.class);
                startActivity(i);
            }
        });

        userlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}
