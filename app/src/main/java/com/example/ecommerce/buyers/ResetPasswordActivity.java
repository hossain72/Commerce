package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextView resetPasswordTV, titleQuestionTV;
    private EditText phoneNumberET, question1ET, question2ET;
    private Button verifyBtn;

    private String check = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check = getIntent().getStringExtra("check");

        resetPasswordTV = findViewById(R.id.resetPasswordTV);
        titleQuestionTV = findViewById(R.id.titleQuestionTV);
        phoneNumberET = findViewById(R.id.findPhoneNumberET);
        question1ET = findViewById(R.id.question1ET);
        question2ET = findViewById(R.id.question2ET);
        verifyBtn = findViewById(R.id.verifyBtn);

    }

    @Override
    protected void onStart() {
        super.onStart();

        phoneNumberET.setVisibility(View.GONE);
        if (check.equals("setting")) {

            resetPasswordTV.setText("Set Question");
            titleQuestionTV.setText("Please set Answer for the Following Security Question?");
            verifyBtn.setText("Set");

            displayPreviousAnswer();

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setAnswer();

                }
            });

        } else if (check.equals("login")) {

            phoneNumberET.setVisibility(View.VISIBLE);

            verifyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();
                }
            });

        }

    }

    private void verifyUser() {

        final String phoneNumber = phoneNumberET.getText().toString();
        final String answer1 = question1ET.getText().toString().toLowerCase();
        final String answer2 = question2ET.getText().toString().toLowerCase();

        if (phoneNumber.equals("")) {

            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();

        } else if (answer1.equals("")) {

            Toast.makeText(this, "Please enter your 1st answer", Toast.LENGTH_SHORT).show();

        } else if (answer2.equals("")) {

            Toast.makeText(this, "Please enter your 2nd answer", Toast.LENGTH_SHORT).show();

        } else {

            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phoneNumber);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        String phone = snapshot.child("phoneNumber").getValue().toString();

                        if (phoneNumber.equals(phone)) {

                            if (snapshot.hasChild("Security Questions")) {

                                String ans1 = snapshot.child("Security Questions").child("answer1").getValue().toString();
                                String ans2 = snapshot.child("Security Questions").child("answer2").getValue().toString();

                                if (!ans1.equals(answer1)) {
                                    Toast.makeText(ResetPasswordActivity.this, "your 1st answer is wrong.", Toast.LENGTH_SHORT).show();
                                } else if (!ans2.equals(answer2)) {
                                    Toast.makeText(ResetPasswordActivity.this, "your 2nd answer is wrong.", Toast.LENGTH_SHORT).show();
                                } else {

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                    builder.setTitle("New Password");

                                    final EditText newPassword = new EditText(ResetPasswordActivity.this);
                                    newPassword.setHint("Write new Password");
                                    builder.setView(newPassword);

                                    builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (!newPassword.getText().toString().equals("")) {

                                                reference.child("password")
                                                        .setValue(newPassword.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(ResetPasswordActivity.this, "Password change successfully ", Toast.LENGTH_SHORT).show();
                                                                    builder.setCancelable(true);
                                                                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                                                                }

                                                            }
                                                        });

                                            } else {
                                                Toast.makeText(ResetPasswordActivity.this, "Please enter password.", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.cancel();

                                        }
                                    });

                                    builder.show();

                                }

                            }

                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "You have not set the security questions.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "This phone number not exists.", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    private void setAnswer() {

        String answer1 = question1ET.getText().toString().toLowerCase();
        String answer2 = question2ET.getText().toString().toLowerCase();

        if (answer1.equals("") && answer2.equals("")) {
            Toast.makeText(ResetPasswordActivity.this, "Please answer both question...", Toast.LENGTH_SHORT).show();
        } else {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(Prevalent.currentOnlineUser.getPhoneNumber());

            Map<String, Object> userDataMap = new HashMap<>();

            userDataMap.put("answer1", answer1);
            userDataMap.put("answer2", answer2);

            reference.child("Security Questions").updateChildren(userDataMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "You have set the security question successfully...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, HomeActivity.class));
                            }

                        }
                    });

        }

    }

    private void displayPreviousAnswer() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(Prevalent.currentOnlineUser.getPhoneNumber());


        reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    question1ET.setText(ans1);
                    question2ET.setText(ans2);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}