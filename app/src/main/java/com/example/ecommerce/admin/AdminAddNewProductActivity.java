package com.example.ecommerce.admin;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, saveCurrentDate, saveCurrentTime, productName, productDescription, productPrice;
    private String productRandomKey, downloadImageUrl;
    private EditText productNameET, productDescriptionET, productPriceET;
    private Button addProductBtn;
    private ImageView productImageIV;
    private static final int GALLERY_PICK = 1;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference productRef;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        productNameET = findViewById(R.id.productNameET);
        productDescriptionET = findViewById(R.id.productDescriptionET);
        productPriceET = findViewById(R.id.productPriceET);
        productImageIV = findViewById(R.id.selectProductIV);
        addProductBtn = findViewById(R.id.addProductBtn);

        categoryName = getIntent().getExtras().get("Category").toString();
        storageReference = FirebaseStorage.getInstance().getReference().child("Product Image");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        builder = new AlertDialog.Builder(this);

        productImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

    }

    private void validateProductData() {

        productName = productNameET.getText().toString();
        productDescription = productDescriptionET.getText().toString();
        productPrice = productPriceET.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product Image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(productName)) {
            productNameET.setError("Please write product name...");
            productNameET.requestFocus();
        } else if (TextUtils.isEmpty(productDescription)) {
            productDescriptionET.setError("Please write product description");
            productDescriptionET.requestFocus();
        } else if (TextUtils.isEmpty(productPrice)) {
            productPriceET.setError("Please write product price...");
            productPriceET.requestFocus();
        } else {
            storeProductInformation();
        }

    }

    private void storeProductInformation() {

        builder.setTitle("Adding New Product");
        builder.setMessage("Please wait, while we are adding the new product...");
        builder.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = storageReference.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                builder.setCancelable(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product upload successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Got the product image url successfully...", Toast.LENGTH_SHORT).show();
                            saveProductInfoToDatabase();
                        }
                    }
                });

            }
        });

    }

    private void saveProductInfoToDatabase() {

        Map<String, Object> productMap = new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("productName", productName);
        productMap.put("productDescription", productDescription);
        productMap.put("productPrice", productPrice);
        productMap.put("image", downloadImageUrl);


        productRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is add successfully...", Toast.LENGTH_SHORT).show();
                            builder.setCancelable(true);
                            startActivity(new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class));
                        } else {
                            Toast.makeText(AdminAddNewProductActivity.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private void openGallery() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            productImageIV.setImageURI(imageUri);
        }

    }
}