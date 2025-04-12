package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Drug;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;

import java.util.UUID;

public class AddDrug extends AppCompatActivity {

    EditText e1,e2,e3,e4,e5;
    Button b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_drug);

        final Session s=new Session(getApplicationContext());

        e1=(EditText)findViewById(R.id.adddrugname);
        e2=(EditText)findViewById(R.id.adddrugprice);
        e3=(EditText)findViewById(R.id.adddrugmanufacture);

        e4=(EditText)findViewById(R.id.adddrugexpirydate);
        e5=(EditText)findViewById(R.id.adddrugdescription);


        b1=(Button)findViewById(R.id.addDrugButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name=e1.getText().toString();
                final String price=e2.getText().toString();
                final String manufacture=e3.getText().toString();
                final String expiry=e4.getText().toString();
                final String description=e5.getText().toString();

                if(name==null || price==null|| manufacture==null|| expiry==null || description==null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String imageName = UUID.randomUUID().toString();

                    Drug drug=new Drug();

                    drug.setName(name);
                    drug.setPrice(price);
                    drug.setManufacture(manufacture);
                    drug.setExpirydate(expiry);
                    drug.setDescription(description);
                    drug.setVendor(s.getusername());
                    drug.setId(new DAO().getUnicKey(Constants.DRUG_DB));

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.DRUG_DB,drug,drug.getId());
                        Toast.makeText(getApplicationContext(),"Drug Added Success",Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(),PharmacyHome.class);
                        startActivity(i);
                    }
                    catch (Exception ex)
                    {
                        Toast.makeText(getApplicationContext(),"Drug Adding Error",Toast.LENGTH_SHORT).show();
                        Log.v("Drug Adding  Ex", ex.toString());
                        ex.printStackTrace();
                    }

                }
            }
        });
    }
}
