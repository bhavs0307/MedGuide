package com.example.medicinereminder.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Map;

public class SearchDrug extends AppCompatActivity {

    EditText e1;
    Button b1;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_drug);
        final Session s = new Session(getApplicationContext());

        Intent i = getIntent();
        savedInstanceState = i.getExtras();
        final String pharmacy = savedInstanceState.getString("pharmacy");

        e1=(EditText)findViewById(R.id.userdrugsearchname);

        listView=(ListView) findViewById(R.id.SearchDrugList);

        b1=(Button)findViewById(R.id.userdrugsearchsubmit);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String drugname=e1.getText().toString();

                if(drugname==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Data", Toast.LENGTH_SHORT).show();
                }
                else if(drugname.length()==0)
                {
                    Toast.makeText(getApplicationContext(),"Please Data",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try
                    {
                        final Map<String,String> viewMap=new HashMap<String,String>();

                        Toast.makeText(getApplicationContext(),"Search:"+drugname,Toast.LENGTH_SHORT).show();

                        final DAO dao=new DAO();
                        dao.getDBReference(Constants.DRUG_DB).addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshotNode : dataSnapshot.getChildren()) {

                                    Drug drug = (Drug) snapshotNode.getValue(Drug.class);

                                    if (drug != null) {

                                        String key=snapshotNode.getKey();
                                        Toast.makeText(getApplicationContext(),"if:"+drug.getName()+"\t"+drugname,Toast.LENGTH_SHORT).show();

                                        if(drug.getVendor().equals(pharmacy)) {
                                            if (drug.getName().toLowerCase().contains(drugname.toLowerCase())) {
                                                viewMap.put(drug.getName() + "_" + drug.getPrice(), key);
                                            }
                                        }
                                    }
                                }

                                ArrayList<String> al = new ArrayList<String>(viewMap.keySet());

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(listView.getContext(),
                                        R.layout.custom_list_item, (al.toArray(new String[al.size()])));

                                listView.setAdapter(adapter);

                                final Session s = new Session(getApplicationContext());
                                s.setViewMap(MapUtil.mapToString(viewMap));
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

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