package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.util.Session;

public class MedicineMenu extends AppCompatActivity {

    Button menuViewMedicine;
    Button medicineMenuBack;
    Button menuUpdateMedicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_menu);

        menuViewMedicine=(Button) findViewById(R.id.menuViewMedicine);
        medicineMenuBack=(Button) findViewById(R.id.medicineMenuBack);
        menuUpdateMedicine=(Button) findViewById(R.id.menuUpdateMedicine);

        final Session s = new Session(getApplicationContext());

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String medicineid = savedInstanceState.getString("medicineid");

        menuViewMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v("in list view action ","");
                Intent i = new Intent(getApplicationContext(),ViewMedicine.class);
                i.putExtra("medicineid",medicineid);
                startActivity(i);
            }
        });

        menuUpdateMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v("in list view action ","");
                Intent i = new Intent(getApplicationContext(),UpdateMedicine.class);
                i.putExtra("medicineid",medicineid);
                startActivity(i);
            }
        });

        medicineMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i= new Intent(getApplicationContext(),UserHome.class);
                startActivity(i);
            }
        });
    }
}