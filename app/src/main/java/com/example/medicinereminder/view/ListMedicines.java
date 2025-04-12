package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Medicine;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.example.medicinereminder.util.MapUtil;

public class ListMedicines extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_medicine);

        listView=(ListView) findViewById(R.id.MedicinesList);
        final Session s=new Session(getApplicationContext());

        final DAO dao=new DAO();

        dao.setDataToAdapterList(listView, Medicine.class, Constants.MEDICINE_DB,s.getusername());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String item = listView.getItemAtPosition(i).toString();
                item= MapUtil.stringToMap(s.getViewMap()).get(item);

                Intent intent=new Intent(getApplicationContext(),MedicineMenu.class);
                intent.putExtra("medicineid",item);
                startActivity(intent);
            }
        });
    }
}
