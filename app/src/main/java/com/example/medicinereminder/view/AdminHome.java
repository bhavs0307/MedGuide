package com.example.medicinereminder.view;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.R;
import com.example.medicinereminder.util.Session;

public class AdminHome extends AppCompatActivity {

    Button adminLogout;
    Button viewPharmacys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        viewPharmacys=(Button) findViewById(R.id.adminviewpharmacys);
        adminLogout=(Button) findViewById(R.id.adminlogout);

        final Session s = new Session(getApplicationContext());

        adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                s.loggingOut();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });

        viewPharmacys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),ListPharmacy.class);
                startActivity(i);
            }
        });
    }
}