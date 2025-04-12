package com.example.medicinereminder.dao;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.medicinereminder.form.Pharmacy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.medicinereminder.form.Medicine; // Import the MedicineDetails class

import com.example.medicinereminder.form.User;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.MapUtil;
import com.example.medicinereminder.util.Session;

public class DAO
{
        public static DatabaseReference getDBReference(String dbName)
        {
            return GetFireBaseConnection.getConnection(dbName);
        }

        public static StorageReference getStorageReference() {
            return GetFireBaseConnection.getStorageReference();
        }

        public String getUnicKey(String dbName)
        {
            return getDBReference(dbName).push().getKey();
        }

        public int addObject(String dbName,Object obj,String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).setValue(obj);

                result=1;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return result;
        }


    public void setDataToAdapterList(final View view, final Class c, final String dbname, final String userid) {

        Session s = new Session(view.getContext());

        final Map<String, Object> map = new HashMap<String, Object>();
        final Map<String, String> viewMap = new HashMap<String, String>();

        getDBReference(dbname).addValueEventListener(new ValueEventListener() {
            int i = 1;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshotNode : dataSnapshot.getChildren()) {

                    String id = snapshotNode.getKey();
                    Object object = snapshotNode.getValue(c);

                    if (dbname.equals(Constants.MEDICAL_SHOP)) {
                        Pharmacy medicalShop = (Pharmacy) object;
                        viewMap.put(i + ")" + medicalShop.getName(), medicalShop.getMobile());
                    }

                    if (dbname.equals(Constants.USER_DB)) {
                        User user = (User) object;
                        viewMap.put(user.getMobile(), user.getMobile());
                    }

                    if (dbname.equals(Constants.MEDICINE_DB)) {

                        Medicine medicine = (Medicine) object;

                        // Null checks before comparing
                        if (medicine.getUserid() != null && s.getusername() != null && medicine.getUserid().equals(s.getusername())) {
                            viewMap.put(i + ")" + medicine.getName(), medicine.getId());
                        }
                    }

                    map.put(id, object);
                    i++;
                }

                ArrayList<String> al = new ArrayList<String>(viewMap.keySet());

                if (view instanceof ListView) {

                    Log.v("in list view setting ", al.toString());

                    final ListView myView = (ListView) view;

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(myView.getContext(),
                            android.R.layout.simple_list_item_1, (al.toArray(new String[al.size()])));

                    myView.setAdapter(adapter);
                }

                s.setViewMap(MapUtil.mapToString(viewMap));

                Log.v("after session setting ", al.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle cancellation if needed
            }
        });

    }

    public int deleteObject(String dbName, String key) {

            int result=0;

            try {

                getDBReference(dbName).child(key).removeValue();

                result=1;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return 0;
        }
    }


