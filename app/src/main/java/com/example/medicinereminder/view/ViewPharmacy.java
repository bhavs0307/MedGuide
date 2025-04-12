package com.example.medicinereminder.view;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Pharmacy;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewPharmacy extends AppCompatActivity {

    Button pharmacyviewdelete;
    Button pharmacyviewback;
    Button pharmacyviewupdatestatus;

    TextView t1,t2,t3,t4,t5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pharmacy);

        final Session s = new Session(getApplicationContext());

        pharmacyviewdelete = (Button) findViewById(R.id.pharmacyviewdelete);
        pharmacyviewback = (Button) findViewById(R.id.pharmacyviewback);
        pharmacyviewupdatestatus = (Button) findViewById(R.id.pharmacyviewupdatestatus);

        t1 = (TextView) findViewById(R.id.pharmacyviewname);
        t2 = (TextView) findViewById(R.id.pharmacyviewmobile);
        t3 = (TextView) findViewById(R.id.pharmacyviewemail);
        t4 = (TextView) findViewById(R.id.pharmacyviewaddress);
        t5 = (TextView) findViewById(R.id.pharmacyviewstatus);

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String pharmacyid = savedInstanceState.getString("pharmacyid");

        if (!s.getRole().equals("admin")) {
            pharmacyviewupdatestatus.setEnabled(false);
            pharmacyviewdelete.setEnabled(false);
        }

        DAO d = new DAO();
        d.getDBReference(Constants.HOSPITAL_DB).child(pharmacyid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Pharmacy pharmacy = dataSnapshot.getValue(Pharmacy.class);

                if (pharmacy != null) {

                    t1.setText("Name :" + pharmacy.getName());
                    t2.setText("Mobile :" + pharmacy.getMobile());
                    t3.setText("Email :" + pharmacy.getEmail());
                    t4.setText("Address :" + pharmacy.getAddress());

                    if (s.getRole().equals("admin")) {
                        t5.setText("Status :" + pharmacy.getStatus());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        pharmacyviewdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final DAO dao = new DAO();
                dao.deleteObject(Constants.HOSPITAL_DB, pharmacyid);

                Intent i = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(i);
            }
        });

        pharmacyviewupdatestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DAO dao = new DAO();
                dao.getDBReference(Constants.HOSPITAL_DB).child(pharmacyid).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Pharmacy pharmacy = dataSnapshot.getValue(Pharmacy.class);

                        if (pharmacy != null) {

                            if(pharmacy.getStatus().equals("yes"))
                            {
                                pharmacy.setStatus("no");
                            }
                            else if(pharmacy.getStatus().equals("no"))
                            {
                                pharmacy.setStatus("yes");
                            }

                            dao.addObject(Constants.HOSPITAL_DB,pharmacy,pharmacy.getUsername());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Intent intent = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(intent);
            }
        });

        pharmacyviewback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),AdminHome.class);
                startActivity(i);
            }
        });
    }
}