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
import com.example.medicinereminder.form.Order;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.MapUtil;
import com.example.medicinereminder.util.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListOrders extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String> adapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_orders);

        listView=(ListView) findViewById(R.id.OrdersList);
        final Session s=new Session(getApplicationContext());

        final Map<String,Object> map=new HashMap<>();
        final Map<String,String> viewMap=new HashMap<String,String>();

        Intent i=getIntent();
        savedInstanceState=i.getExtras();

        final DAO dao=new DAO();

        if(s.getRole().equals("pharmacy"))
        {
            dao.getDBReference(Constants.ORDERS).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int j=1;

                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                        String key=snapshotNode.getKey();
                        Order order=snapshotNode.getValue(Order.class);

                        if(order!=null && order.getVendor().equals(s.getusername()))
                        {
                            viewMap.put(j+"_"+order.getDrug(),key);
                            j++;
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
        }
        else if(s.getRole().equals("user"))
        {
            dao.getDBReference(Constants.ORDERS).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int j=1;

                    for (DataSnapshot snapshotNode: dataSnapshot.getChildren()) {

                        String key=snapshotNode.getKey();
                        Order order=snapshotNode.getValue(Order.class);

                        if(order!=null && order.getUser().equals(s.getusername()))
                        {
                            viewMap.put(j+"_"+order.getDrugName(),key);
                            j++;
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
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                String id= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent = new Intent(getApplicationContext(), ViewOrder.class);
                intent.putExtra("order", id);
                startActivity(intent);
            }
        });

    }
}
