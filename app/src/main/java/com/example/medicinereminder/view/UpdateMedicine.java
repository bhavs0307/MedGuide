package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Medicine;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;

public class UpdateMedicine extends AppCompatActivity {

    EditText updateMedicineStock;
    Button updateMedicineSubmit;
    Button updateMedicineCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_update_medicine);

        final Session s = new Session(getApplicationContext());

        updateMedicineStock = (EditText) findViewById(R.id.updateMedicineStock);
        updateMedicineSubmit = (Button) findViewById(R.id.updateMedicineSubmit);
        updateMedicineCancel = (Button) findViewById(R.id.updateMedicineCancel);

        Intent i = getIntent();
        savedInstanceState = i.getExtras();

        final String medicineid = savedInstanceState.getString("medicineid");

        updateMedicineSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stock= updateMedicineStock.getText().toString();

                if (stock == null) {
                    Toast.makeText(getApplicationContext(), "Please Enter Stock", Toast.LENGTH_SHORT).show();
                } else {

                    final DAO d=new DAO();
                    d.getDBReference(Constants.MEDICINE_DB).child(medicineid).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Medicine medicine =dataSnapshot.getValue(Medicine.class);

                            if(medicine !=null)
                            {
                                medicine.setStockcount(stock);
                                d.addObject(Constants.MEDICINE_DB, medicine,medicineid);

                                Intent i = new Intent(getApplicationContext(),UserHome.class);
                                startActivity(i);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        updateMedicineCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),UserHome.class);
                startActivity(i);
            }
        });
    }
}
