package com.example.medicinereminder.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.R;
import com.example.medicinereminder.util.Session;

public class PharmacyHome extends AppCompatActivity {

    Button pharmacyLogout;
    Button vieworders;
    Button viewdrugs;
    Button adddrug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_home);

        vieworders=(Button) findViewById(R.id.pharmacyvieworders);
        viewdrugs=(Button) findViewById(R.id.pharmacyviewdrugs);
        adddrug=(Button) findViewById(R.id.pharmacyadddrug);
        pharmacyLogout=(Button) findViewById(R.id.pharmacylogout);

        final Session s = new Session(getApplicationContext());

        adddrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),AddDrug.class);
                startActivity(i);
            }
        });
        viewdrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListDrug.class);
                startActivity(i);
            }
        });
        vieworders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ListOrders.class);
                startActivity(i);
            }
        });
        pharmacyLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s.loggingOut();
                s.setRole("");
                s.setusername("");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
    }
}