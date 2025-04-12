package com.example.medicinereminder.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.view.CardForm;
import com.example.medicinereminder.R;
import com.example.medicinereminder.dao.DAO;
import com.example.medicinereminder.form.Order;
import com.example.medicinereminder.util.Constants;
import com.example.medicinereminder.util.Session;

import java.util.Date;

public class PaymentActivity extends AppCompatActivity {

    CardForm cardForm;
    Button buy;
    AlertDialog.Builder alertBuilder;
    TextView t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        final Session session=new Session(getApplicationContext());
        Intent i = getIntent();
        savedInstanceState = i.getExtras();

        t1 = (TextView) findViewById(R.id.totalamt);

        final String drug = savedInstanceState.getString("drug");
        final String vendor = savedInstanceState.getString("vendor");
        final String price = savedInstanceState.getString("price");
        final String pname = savedInstanceState.getString("pname");

        t1.setText("Total Amount:"+price);

        cardForm = findViewById(R.id.card_form);
        buy = findViewById(R.id.btnBuy);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(PaymentActivity.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(PaymentActivity.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv() + "\n" +
                            "Postal code: " + cardForm.getPostalCode() + "\n" +
                            "Phone number: " + cardForm.getMobileNumber());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                            Order order=new Order();

                            order.setId(new DAO().getUnicKey(Constants.ORDERS));
                            order.setDrug(drug);
                            order.setStatus("waiting");
                            order.setUser(session.getusername());
                            order.setDateoforder(new Date().toString());
                            order.setVendor(vendor);
                            order.setDrugName(pname);

                            new DAO().addObject(Constants.ORDERS,order,order.getId());

                            Intent intent=new Intent(getApplicationContext(),UserHome.class);
                            startActivity(intent);

                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Please complete the form", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}