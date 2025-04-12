package com.example.medicinereminder.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Medicine;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ViewMedicine extends AppCompatActivity {

    Button menuDeleteMedicine;
    Button viewMedicineBack;
    ImageView imageView;

    TextView t1, t2, t3, t4, t5;
    String medicineid;  // Declare this at the class level
    DAO dao;  // Declare DAO at the class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicine);

        menuDeleteMedicine = findViewById(R.id.menuDeleteMedicine);
        viewMedicineBack = findViewById(R.id.viewMedicineBack);

        t1 = findViewById(R.id.medicineviewname);
        t2 = findViewById(R.id.medicineviewstock);
        t3 = findViewById(R.id.medicineviewhour);
        t4 = findViewById(R.id.medicineviewtime);
        t5 = findViewById(R.id.medicineviewusage);

        imageView = findViewById(R.id.medicineviewimage);
        final Session s = new Session(getApplicationContext());

        // Get Intent and extract the medicineid
        Intent i = getIntent();
        if (i != null && i.getExtras() != null) {
            medicineid = i.getStringExtra("medicineid");  // Assign it to the class variable

            if (medicineid != null) {
                dao = new DAO();  // Initialize DAO here

                dao.getDBReference(Constants.MEDICINE_DB).child(medicineid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Medicine medicine = dataSnapshot.getValue(Medicine.class);

                        if (medicine != null) {
                            t1.setText("Medicine Name: " + (medicine.getName() != null ? medicine.getName() : "N/A"));
                            t2.setText("Medicine Stock: " + (medicine.getStockcount() != null ? medicine.getStockcount() : "N/A"));
                            t3.setText("Medicine Hour: " + (medicine.getHour() != null ? medicine.getHour() : "N/A"));
                            t4.setText("Medicine Time: " + (medicine.getTime() != null ? medicine.getTime() : "N/A"));
                            t5.setText("Medicine Usage: " + (medicine.getUsage() != null ? medicine.getUsage() : "N/A"));

                            // Load image from Firebase Storage
                            StorageReference ref = DAO.getStorageReference().child("images/" + medicine.getImage());
                            long ONE_MEGABYTE = 1024 * 1024 * 5;
                            ref.getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            if (bm != null) {
                                                imageView.setImageBitmap(bm);
                                            } else {
                                                Log.v("ViewMedicine", "Bitmap is null");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Log.v("ViewMedicine", "Image loading failed: " + exception.getMessage());
                                        }
                                    });
                        } else {
                            Log.e("ViewMedicine", "Medicine data is null");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("ViewMedicine", "Database error: " + databaseError.getMessage());
                    }
                });
            } else {
                Log.e("ViewMedicine", "Medicine ID is null or not provided");
            }
        }

        // Handle delete button click
        menuDeleteMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (medicineid != null) {
                    dao.deleteObject(Constants.MEDICINE_DB, medicineid);
                    Intent intent = new Intent(getApplicationContext(), UserHome.class);
                    startActivity(intent);
                } else {
                    Log.e("ViewMedicine", "Medicine ID is null, cannot delete");
                }
            }
        });

        // Handle back button click to finish the activity and return to previous
        viewMedicineBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Custom back handling
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Custom back button handling (you can log here or add custom behavior if needed)
        Log.d("ViewMedicine", "Back button pressed");
        super.onBackPressed(); // Finish the activity and return to the previous one
    }
}