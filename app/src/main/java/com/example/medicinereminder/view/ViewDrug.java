package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Drug;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewDrug extends AppCompatActivity {

    TextView t1,t2,t3,t4,t5;

    Button back;
    Button delete;

    Button viewDrugorderdrug;
    Button viewDrugvieworders;

    String price="";
    String vendor="";
    String drugName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_drug);

        back=(Button) findViewById(R.id.viewdrugback);
        delete=(Button) findViewById(R.id.viewDrugDelete);

        viewDrugorderdrug=(Button) findViewById(R.id.viewDrugorderdrug);
        viewDrugvieworders=(Button) findViewById(R.id.viewDrugvieworders);

        final Session session=new Session(getApplicationContext());
        final String role=session.getRole();

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String pid=savedInstanceState.getString("drug");

        t1=(TextView) findViewById(R.id.viewdrugname);
        t2=(TextView)findViewById(R.id.viewdrugprice);
        t3=(TextView) findViewById(R.id.viewdrugmanufacture);
        t4=(TextView)findViewById(R.id.viewdrugexpirydate);
        t5=(TextView) findViewById(R.id.viewdrugdescription);

        DAO d=new DAO();
        d.getDBReference(Constants.DRUG_DB).child(pid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Drug drug=dataSnapshot.getValue(Drug.class);

                if(drug!=null)
                {
                    price=drug.getPrice();
                    vendor=drug.getVendor();
                    drugName=drug.getName();

                    if(!drug.getVendor().equals(session.getusername()))
                    {
                        delete.setEnabled(false);
                        viewDrugvieworders.setEnabled(false);
                    }

                    if(drug.getVendor().equals(session.getusername()))
                    {
                        viewDrugorderdrug.setEnabled(false);
                    }

                    t1.setText("Drug Name :"+drug.getName());
                    t2.setText("Price :"+drug.getPrice());
                    t3.setText("manufacture :"+drug.getManufacture());
                    t4.setText("expirydate :"+drug.getExpirydate());
                    t5.setText("description :"+drug.getDescription());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String role=session.getRole();

                if(role.equals("pharmacy"))
                {
                    Intent i=new Intent(getApplicationContext(),PharmacyHome.class);
                    startActivity(i);
                }
                else if(role.equals("user"))
                {
                    Intent i=new Intent(getApplicationContext(), UserHome.class);
                    startActivity(i);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DAO d=new DAO();
                d.deleteObject(Constants.DRUG_DB,pid);

                Intent i=new Intent(getApplicationContext(),PharmacyHome.class);
                startActivity(i);
            }
        });

        viewDrugorderdrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),PaymentActivity.class);
                i.putExtra("drug",pid);
                i.putExtra("price",price);
                i.putExtra("vendor",vendor);
                i.putExtra("pname",drugName);
                startActivity(i);
            }
        });

        viewDrugvieworders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),ListOrders.class);
                i.putExtra("drug",pid);
                startActivity(i);
            }
        });

    }
}
