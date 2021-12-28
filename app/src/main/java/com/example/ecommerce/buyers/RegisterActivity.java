package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountBtn;
    private EditText nameET, phoneNumberET, passwordET;
    private AlertDialog.Builder builder;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountBtn = findViewById(R.id.createAccountBtn);
        nameET = findViewById(R.id.registerNameET);
        phoneNumberET = findViewById(R.id.registerPhoneNumberET);
        passwordET = findViewById(R.id.registerPasswordET);

        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {

        final String name = nameET.getText().toString();
        final String phoneNumber = phoneNumberET.getText().toString();
        final String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(name)) {
            nameET.setError("Please enter your name...");
            nameET.requestFocus();
        } else if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberET.setError("Please enter your phone number...");
            phoneNumberET.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordET.setError("Please enter your password...");
            passwordET.requestFocus();
        } else {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Create Account");
            builder.setMessage("Please wait, while we are checking the credential.");
            builder.show();

            final DatabaseReference rootRef;
            rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (!(snapshot.child(parentDbName).child(phoneNumber).exists())) {

                        Map<String, Object> userDataMap = new HashMap<>();

                        userDataMap.put("phoneNumber", phoneNumber);
                        userDataMap.put("password", password);
                        userDataMap.put("name", name);

                        rootRef.child(parentDbName).child(phoneNumber).updateChildren(userDataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Congratulation, your account  has been created.", Toast.LENGTH_SHORT).show();
                                            builder.setCancelable(true);
                                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        } else {
                                            builder.setCancelable(true);
                                            Toast.makeText(RegisterActivity.this, "Network error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "This" + phoneNumber + " already exists.", Toast.LENGTH_SHORT).show();
                        builder.setCancelable(true);
                        Toast.makeText(RegisterActivity.this, "Please try again using another phone number.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
}