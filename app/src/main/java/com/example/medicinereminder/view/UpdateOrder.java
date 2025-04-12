package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Order;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UpdateOrder extends AppCompatActivity {

    EditText updateOrderStatus;
    Button updateOrderSubmit;
    Button updateOrderCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_order);

        final Session s = new Session(getApplicationContext());

        updateOrderStatus = (EditText) findViewById(R.id.updateOrderStatus);
        updateOrderSubmit = (Button) findViewById(R.id.updateOrderSubmit);
        updateOrderCancel = (Button) findViewById(R.id.updateOrderCancel);

        Intent i = getIntent();
        savedInstanceState = i.getExtras();

        final String orderid = savedInstanceState.getString("order");

        updateOrderSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status = updateOrderStatus.getText().toString();

                if (status == null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Order Status", Toast.LENGTH_SHORT).show();
                } else {

                    DAO dao = new DAO();
                    dao.getDBReference(Constants.ORDERS).child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Order order=dataSnapshot.getValue(Order.class);

                            if(order!=null)
                            {
                                order.setStatus(status);
                                dao.addObject(Constants.ORDERS,order,order.getId());

                                Intent intent=new Intent(getApplicationContext(),PharmacyHome.class);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        updateOrderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),PharmacyHome.class);
                startActivity(intent);
            }
        });
    }
}
