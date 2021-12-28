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
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.admin.AdminCategoryActivity;
import com.example.ecommerce.model.User;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneNumberET, passwordET;
    private Button loginBtn;
    private CheckBox checkBox;
    private AlertDialog.Builder builder;
    private TextView adminLinkTV, notAdminLinkTV, forgetPasswordTV;
    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneNumberET = findViewById(R.id.loginPhoneNumberET);
        passwordET = findViewById(R.id.loginPasswordET);
        loginBtn = findViewById(R.id.loginBtn);
        checkBox = findViewById(R.id.rememberMeCB);
        adminLinkTV = findViewById(R.id.adminPanelLink);
        notAdminLinkTV = findViewById(R.id.notAdminPanelLink);
        forgetPasswordTV = findViewById(R.id.forgetPasswordTV);

        Paper.init(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        adminLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login Admin");
                adminLinkTV.setVisibility(View.INVISIBLE);
                notAdminLinkTV.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        notAdminLinkTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginBtn.setText("Login");
                notAdminLinkTV.setVisibility(View.INVISIBLE);
                adminLinkTV.setVisibility(View.VISIBLE);
                parentDbName = "Users";
            }
        });

        forgetPasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "login");
                startActivity(intent);
            }
        });

    }

    private void login() {

        final String phoneNumber = phoneNumberET.getText().toString();
        final String password = passwordET.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberET.setError("Please enter your phone number...");
            phoneNumberET.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            passwordET.setError("Please enter your password...");
            passwordET.requestFocus();
        } else {

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Login Account");
            builder.setMessage("Please wait, while we are checking the credential.");
            builder.show();

            allowAccessToAccount(phoneNumber, password);

        }

    }

    private void allowAccessToAccount(final String phoneNumber, final String password) {

        if (checkBox.isChecked()) {

            Paper.book().write(Prevalent.userPhoneKey, phoneNumber);
            Paper.book().write(Prevalent.userPasswordKey, password);

        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDbName).child(phoneNumber).exists()) {

                    User user = snapshot.child(parentDbName).child(phoneNumber).getValue(User.class);

                    if (user.getPhoneNumber().equals(phoneNumber)) {

                        if (user.getPassword().equals(password)) {

                            if (parentDbName.equals("Admins")) {

                                Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                builder.setCancelable(true);
                                startActivity(new Intent(LoginActivity.this, AdminCategoryActivity.class));

                            } else if (parentDbName.equals("Users")) {

                                Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                builder.setCancelable(true);
                                Prevalent.currentOnlineUser = user;
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                            }

                        } else {

                            Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_SHORT).show();
                            builder.setCancelable(true);

                        }
                    }

                } else {

                    Toast.makeText(LoginActivity.this, "Account with this " + phoneNumber + " number do not exists.", Toast.LENGTH_SHORT).show();
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