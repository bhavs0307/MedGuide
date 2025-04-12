package com.example.medicinereminder.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Pharmacy;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class PharmacyLogin extends AppCompatActivity {

    private Session session;
    EditText e1,e2;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new Session(getApplicationContext());

        setContentView(R.layout.activity_pharmacy_login);

        e1=(EditText)findViewById(R.id.pharmacyusername);
        e2=(EditText)findViewById(R.id.pharmacypassword);
        b1=(Button)findViewById(R.id.pharmacyloginsubmit);

        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String username=e1.getText().toString();
                final String password=e2.getText().toString();

                if(username==null|| password==null || username.length()<=0|| password.length()<=0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter UserName and Password", Toast.LENGTH_SHORT).show();
                }
                else if (username.equals("admin") && password.equals("admin")) {
                        session.setusername("admin");
                        session.setRole("admin");
                        Intent i = new Intent(getApplicationContext(), AdminHome.class);
                        startActivity(i);

                    }
                else {

                        DAO d = new DAO();
                        d.getDBReference(Constants.HOSPITAL_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Pharmacy pharmacy = (Pharmacy) dataSnapshot.getValue(Pharmacy.class);

                            if (pharmacy == null) {
                                Toast.makeText(getApplicationContext(), "Invalid User Name ", Toast.LENGTH_SHORT).show();
                            } else if (pharmacy != null && pharmacy.getPassword().equals(password)) {

                                if (pharmacy.getStatus().equals("yes")) {

                                    session.setusername(username);
                                    session.setRole("pharmacy");

                                    Intent i = new Intent(getApplicationContext(),PharmacyHome.class);
                                    startActivity(i);

                                } else {
                                    Toast.makeText(getApplicationContext(), "your Account is Not yet Activated", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "In valid Password", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}