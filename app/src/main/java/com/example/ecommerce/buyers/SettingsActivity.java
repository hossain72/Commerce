package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private static final int RESULT_LOAD_IMG = 1;
    private CircleImageView profileImage;
    private TextView closeTextBtn, updateTextBtn, profileChangeTextBtn;
    private EditText nameET, phoneNumberET, addressET;
    private Button securityQuestionBtn;

    private Uri imageUri;
    private StorageReference profilePictureStorageRef;
    private String myUrl = "";
    private String checker = "";
    private StorageTask uploadTask;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profilePictureStorageRef =
                FirebaseStorage.getInstance().getReference().child("Profile pictures");
        profileImage = findViewById(R.id.profileImageSetting);
        closeTextBtn = findViewById(R.id.closeTextBtn);
        updateTextBtn = findViewById(R.id.updateTextBtn);
        profileChangeTextBtn = findViewById(R.id.profileChangeTextBtn);
        nameET = findViewById(R.id.changeNameET);
        phoneNumberET = findViewById(R.id.changePhoneNumberET);
        addressET = findViewById(R.id.changeAddressET);
        securityQuestionBtn = findViewById(R.id.securityQuestionBtn);

        userInfoDisplay(profileImage, nameET, phoneNumberET, addressET);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);


            }
        });

        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check", "setting");
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error, Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }

    }

    private void updateOnlyUserInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", nameET.getText().toString());
        userMap.put("phoneNumber", phoneNumberET.getText().toString());
        userMap.put("address", addressET.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhoneNumber()).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        finish();

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(nameET.getText().toString())) {
            nameET.setError("Name is mandatory...!");
            nameET.requestFocus();
        } else if (TextUtils.isEmpty(addressET.getText().toString())) {
            addressET.setError("Address is mandatory...");
            addressET.requestFocus();
        } else if (TextUtils.isEmpty(phoneNumberET.getText().toString())) {
            phoneNumberET.setError("Phone Number is mandatory...");
            phoneNumberET.requestFocus();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Profile...");
        builder.setMessage("Please wait, while we are updating your account information.");
        builder.show();

        if (imageUri != null) {
            final StorageReference fileRef = profilePictureStorageRef
                    .child(Prevalent.currentOnlineUser.getPhoneNumber() + ".jpg");
            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name", nameET.getText().toString());
                        userMap.put("phoneOrder", phoneNumberET.getText().toString());
                        userMap.put("image", myUrl);
                        userMap.put("address", addressET.getText().toString());
                        ref.child(Prevalent.currentOnlineUser.getPhoneNumber()).updateChildren(userMap);

                        builder.setCancelable(true);
                        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                        finish();

                    } else {
                        builder.setCancelable(true);
                        Toast.makeText(SettingsActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Image is not selected...", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImage, final EditText nameET, final EditText phoneNumberET, final EditText addressET) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhoneNumber());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    if (snapshot.child("image").exists()) {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phoneNumber = snapshot.child("phoneNumber").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImage);
                        nameET.setText(name);
                        phoneNumberET.setText(phoneNumber);
                        addressET.setText(address);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}