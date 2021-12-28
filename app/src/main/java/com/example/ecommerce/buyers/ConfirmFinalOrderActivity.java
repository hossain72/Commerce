package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText orderNameET, orderPhoneNumberET, orderAddressET, orderCityET;
    private Button orderConfirmBtn;
    private String totalPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPrice = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "" + totalPrice, Toast.LENGTH_SHORT).show();

        orderNameET = findViewById(R.id.orderNameET);
        orderPhoneNumberET = findViewById(R.id.orderPhoneNumberET);
        orderAddressET = findViewById(R.id.orderAddressET);
        orderCityET = findViewById(R.id.orderCityET);
        orderConfirmBtn = findViewById(R.id.orderConfirmBtn);

        orderConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });

    }

    private void check() {

        if (TextUtils.isEmpty(orderNameET.getText().toString())) {
            orderNameET.setError("Please enter your name...");
            orderNameET.requestFocus();
        } else if (TextUtils.isEmpty(orderPhoneNumberET.getText().toString())) {
            orderPhoneNumberET.setError("Please enter your phone number...");
            orderPhoneNumberET.requestFocus();
        } else if (TextUtils.isEmpty(orderAddressET.getText().toString())) {
            orderPhoneNumberET.setError("Please enter your address...");
            orderPhoneNumberET.requestFocus();
        } else if (TextUtils.isEmpty(orderCityET.getText().toString())) {
            orderCityET.setError("Please enter your city...");
            orderCityET.requestFocus();
        } else {
            confirmOrder();
        }

    }

    private void confirmOrder() {

        final String saveCurrentTime, saveCurrentDate;

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhoneNumber());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("totalPrice", totalPrice);
        orderMap.put("orderName", orderNameET.getText().toString());
        orderMap.put("orderPhoneNUmber", orderPhoneNumberET.getText().toString());
        orderMap.put("orderAddress", orderAddressET.getText().toString());
        orderMap.put("orderCity", orderCityET.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("time", saveCurrentTime);
        orderMap.put("state", "not order");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhoneNumber())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });

    }
}