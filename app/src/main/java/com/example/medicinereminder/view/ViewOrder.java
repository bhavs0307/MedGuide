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
import com.example.medicinereminder.form.Order;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ViewOrder extends AppCompatActivity {

    TextView t1,t2,t3,t4,t5,t6,t7;

    Button back;
    Button delete;
    Button updateorderstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);

        back=(Button) findViewById(R.id.vieworderback);
        delete=(Button) findViewById(R.id.viewOrderDelete);

        updateorderstatus=(Button) findViewById(R.id.updateorderstatus);

        final Session session=new Session(getApplicationContext());
        final String role=session.getRole();

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final String order=savedInstanceState.getString("order");

        t1=(TextView) findViewById(R.id.vieworderdrugname);
        t2=(TextView)findViewById(R.id.vieworderdrugprice);
        t3=(TextView) findViewById(R.id.vieworderdrugmanufacture);
        t4=(TextView)findViewById(R.id.vieworderdrugdescription);
        t5=(TextView) findViewById(R.id.vieworderuser);
        t6=(TextView)findViewById(R.id.vieworderdateoforder);
        t7=(TextView)findViewById(R.id.vieworderstatus);

        if(session.getRole().equals("user"))
        {
            updateorderstatus.setEnabled(false);
        }
        else if(session.getRole().equals("pharmacy"))
        {
            delete.setEnabled(false);
        }

        DAO d=new DAO();
        d.getDBReference(Constants.ORDERS).child(order).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Order order=dataSnapshot.getValue(Order.class);

                if(order!=null)
                {
                    final DAO dao=new DAO();
                    dao.getDBReference(Constants.DRUG_DB).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                                String key=snapshotNode.getKey();
                                Drug drug=snapshotNode.getValue(Drug.class);

                                if(drug!=null)
                                {
                                    t1.setText("Drug Name :"+drug.getName());
                                    t2.setText("Price :"+drug.getPrice());
                                    t3.setText("manufacture :"+drug.getManufacture());
                                    t5.setText("description :"+drug.getDescription());
                                    t6.setText("Order By :"+order.getUser());
                                    t7.setText("Date of Order :"+order.getDateoforder());
                                    t7.setText("Order Status :"+order.getStatus());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });
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
                d.deleteObject(Constants.ORDERS,order);

                Intent i=new Intent(getApplicationContext(),PharmacyHome.class);
                startActivity(i);
            }
        });

        updateorderstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(getApplicationContext(),UpdateOrder.class);
                i.putExtra("order",order);
                startActivity(i);
            }
        });
    }
}
