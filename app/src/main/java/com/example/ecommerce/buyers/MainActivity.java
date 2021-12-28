package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.model.User;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowBtn, loginBtn;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        joinNowBtn = findViewById(R.id.joinNowBtn);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        String userPhoneKey = Paper.book().read(Prevalent.userPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.userPasswordKey);

        if (userPhoneKey != null && userPasswordKey != "") {
            if (!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)) {

                builder = new AlertDialog.Builder(this);
                builder.setTitle("Already Logged in");
                builder.setMessage("Please wait...");
                builder.show();

                allowAccess(userPhoneKey, userPasswordKey);

            }
        }

    }

    private void allowAccess(final String phoneNumber, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("Users").child(phoneNumber).exists()) {

                    User user = snapshot.child("Users").child(phoneNumber).getValue(User.class);

                    if (user.getPhoneNumber().equals(phoneNumber)) {
                        if (user.getPassword().equals(password)) {
                            Prevalent.currentOnlineUser = user;
                            Toast.makeText(MainActivity.this, "Please wait, You are already logged in.", Toast.LENGTH_SHORT).show();
                            builder.setCancelable(true);
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            builder.setCancelable(true);
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Account with this " + phoneNumber + " number do not exists.", Toast.LENGTH_SHORT).show();
                    builder.setCancelable(true);
                    //Toast.makeText(LoginActivity.this, "You need to create a new Account.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}