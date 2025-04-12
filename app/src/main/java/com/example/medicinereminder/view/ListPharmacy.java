package com.example.medicinereminder.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Pharmacy;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListPharmacy extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.medicinereminder.R.layout.activity_list_pharmacy);

        listView=(ListView) findViewById(R.id.PharmacyList);
        final Session s = new Session(getApplicationContext());

        final DAO dao=new DAO();
        dao.getDBReference(Constants.HOSPITAL_DB).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> al=new ArrayList<String>();

                for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                    Pharmacy pharmacy = (Pharmacy) snapshotNode.getValue(Pharmacy.class);

                    if (s.getRole().equals("admin")) {
                        al.add(pharmacy.getUsername());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                        android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String pharmacy = listView.getItemAtPosition(position).toString();

                Intent intent=new Intent(getApplicationContext(),ViewPharmacy.class);
                intent.putExtra("pharmacyid",pharmacy);
                startActivity(intent);
            }
        });
    }
}