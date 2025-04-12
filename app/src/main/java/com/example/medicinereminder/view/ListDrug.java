package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Drug;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.MapUtil;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListDrug extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drug);

        listView = (ListView) findViewById(R.id.DrugsList);

        final Session s = new Session(getApplicationContext());
        final Map<String,String> viewMap=new HashMap<String,String>();

        final DAO dao=new DAO();
        dao.getDBReference(Constants.DRUG_DB).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                    String key=snapshotNode.getKey();
                    Drug drug=snapshotNode.getValue(Drug.class);

                    if(drug!=null && drug.getVendor().equals(s.getusername()))
                    {
                        viewMap.put(drug.getName()+"_"+drug.getPrice(),key);
                    }
                }

                ArrayList<String> al=new ArrayList<String>(viewMap.keySet());

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                        android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                listView.setAdapter(adapter);

                final Session s = new Session(getApplicationContext());
                s.setViewMap(MapUtil.mapToString(viewMap));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                String id= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent = new Intent(getApplicationContext(), ViewDrug.class);
                intent.putExtra("drug", id);
                startActivity(intent);
            }
        });
    }
}
