package com.example.medicinereminder.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.IOException;

public class ViewNotification extends AppCompatActivity {

    ImageView imageView;
    Button stopalarm;
    private MediaPlayer player;
    private final Context context = this;

    TextView t1, t2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_view_notification);

        stopalarm = findViewById(R.id.stopalarm);
        t1 = findViewById(R.id.medicineviewname1);
        t2 = findViewById(R.id.medicineviewusage1);
        imageView = findViewById(R.id.medicineviewimage1);

        play(this, getAlarmSound());

        final Session s = new Session(getApplicationContext());

        Intent intent = getIntent();
        if (intent == null || intent.getExtras() == null) {
            Toast.makeText(this, "No medicine details available.", Toast.LENGTH_SHORT).show();
            return;
        }

        String medicineid = intent.getExtras().getString("medicineid");
        if (medicineid == null) {
            Toast.makeText(this, "Medicine ID is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        DAO dao = new DAO();
        dao.getDBReference(Constants.MEDICINE_DB).child(medicineid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Medicine medicine = dataSnapshot.getValue(Medicine.class);
                if (medicine != null) {
                    t1.setText("Medicine Name: " + medicine.getName());
                    t2.setText("Medicine Usage: " + medicine.getUsage());

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
                                        Log.v("voidmain", "Bitmap is null");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(ViewNotification.this, "Failed to load image.", Toast.LENGTH_SHORT).show();
                                    Log.v("voidmain", "Image reading failure");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewNotification.this, "Failed to load medicine details.", Toast.LENGTH_SHORT).show();
            }
        });

        stopalarm.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (player != null) {
                    player.stop();
                    player.release();
                }
                Intent i = new Intent(context, UserHome.class);
                startActivity(i);
                finish(); // Close current activity
                return false;
            }
        });
    }

    private void play(Context context, Uri alert) {
        player = new MediaPlayer();
        try {
            player.setDataSource(context, alert);
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audio != null && audio.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                player.setAudioStreamType(AudioManager.STREAM_ALARM);
                player.prepare();
                player.start();
            }
        } catch (IOException e) {
            Log.e("Error", "Failed to play alarm sound.");
        }
    }

    private Uri getAlarmSound() {
        Uri alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alertSound == null) {
            alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alertSound == null) {
                alertSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alertSound;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
