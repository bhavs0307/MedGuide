package com.example.medicinereminder.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.User;
import com.example.medicinereminder.util.Constants;

public class RegisterActivity extends AppCompatActivity {

    EditText e1,e2,e3;
    Button b1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        e1=(EditText)findViewById(R.id.registerMobile);
        e2=(EditText)findViewById(R.id.registerPassword);
        e3=(EditText)findViewById(R.id.registerConPass);

        b1=(Button)findViewById(R.id.registerButton);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mobile=e1.getText().toString();
                String password=e2.getText().toString();
                String conformPassword=e3.getText().toString();


                if(password==null|| conformPassword==null|| mobile==null)
                {
                    Toast.makeText(getApplicationContext(),"Please Enter Valid Data",Toast.LENGTH_SHORT).show();
                }
                else if(mobile.length()<10|| mobile.length()>12)
                {
                    Toast.makeText(getApplicationContext(),"Invalid Mobile",Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(conformPassword))
                {
                    Toast.makeText(getApplicationContext(),"Password Mismatch",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    User user=new User();
                    user.setPassword(password);
                    user.setMobile(mobile);

                    DAO dao=new DAO();

                    try
                    {
                        dao.addObject(Constants.USER_DB,user,user.getMobile());

                        Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();

                        Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(i);
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
    }
}
