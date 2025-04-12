package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.MainActivity;
import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Pharmacy;
import com.example.medicinereminder.util.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class PharmacyRegistration extends AppCompatActivity{

    EditText e1,e2,e3,e4,e5,e6,e7;
    Button b1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pharmacy_registration);

        e1=(EditText)findViewById(R.id.regpharmacyusername);
        e2=(EditText)findViewById(R.id.regpharmacypassword);
        e3=(EditText)findViewById(R.id.regpharmacyconformpassword);
        e4=(EditText)findViewById(R.id.regpharmacyemail);
        e5=(EditText)findViewById(R.id.regpharmacymobile);
        e6=(EditText)findViewById(R.id.regpharmacyname);
        e7=(EditText)findViewById(R.id.regpharmacyaddress);

        b1=(Button)findViewById(R.id.regpharmacysubmit);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();
                String email=e4.getText().toString();
                String mobile=e5.getText().toString();
                String name=e6.getText().toString();
                String address=e7.getText().toString();

                if(username==null|| password==null|| conformPassword==null|| email==null|| mobile==null|| name==null || address==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(username.length()==0|| password.length()==0|| conformPassword.length()==0|| email.length()==0|| mobile.length()==0|| name.length()==0 || address.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()!=10)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(conformPassword))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else if (!isValidEmail(email)) {
                    Toast.makeText(getApplicationContext(),"Enter Valid Email",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Pharmacy pharmacy=new Pharmacy();

                    pharmacy.setUsername(username);
                    pharmacy.setPassword(password);
                    pharmacy.setEmail(email);
                    pharmacy.setMobile(mobile);
                    pharmacy.setName(name);
                    pharmacy.setAddress(address);
                    pharmacy.setStatus("no");

                    try
                    {

                        DAO dao = new DAO();
                        dao.getDBReference(Constants.HOSPITAL_DB).child(username).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Pharmacy pharmacy1 = (Pharmacy) dataSnapshot.getValue(Pharmacy.class);

                                if (pharmacy1 == null) {

                                    dao.addObject(Constants.HOSPITAL_DB,pharmacy,pharmacy.getUsername());

                                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();

                                    Intent i=new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);

                                }else {
                                    Toast.makeText(getApplicationContext(), "Pharmacy All ready Registered", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Register Error",Toast.LENGTH_SHORT).show();
                        Log.v("User Registration Ex", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });
    }

    public static boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
