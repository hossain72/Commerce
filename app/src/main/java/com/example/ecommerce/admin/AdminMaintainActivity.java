package com.example.ecommerce.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class AdminMaintainActivity extends AppCompatActivity {

    private EditText productNameET, productDescriptionET, productPriceET;
    private ImageView productImage;
    private Button applyChangeBtn, deleteProductBtn;
    private String productId = "";
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain);

        productId = getIntent().getStringExtra("pid");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        productImage = findViewById(R.id.maintainImageTV);
        productNameET = findViewById(R.id.maintainNameET);
        productDescriptionET = findViewById(R.id.maintainDescriptionET);
        productPriceET = findViewById(R.id.maintainPriceET);
        applyChangeBtn = findViewById(R.id.changeApplyBtn);
        deleteProductBtn = findViewById(R.id.adminDeleteProductBtn);

        applyChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProductInfo();
            }
        });

        deleteProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();
            }
        });

        displaySpecificProductInfo();

    }

    private void deleteProduct() {

        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(AdminMaintainActivity.this, AdminCategoryActivity.class));
                finish();
                Toast.makeText(AdminMaintainActivity.this, "The product is delete successfully.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeProductInfo() {

        String productName = productNameET.getText().toString();
        String productDescription = productDescriptionET.getText().toString();
        String productPrice = productPriceET.getText().toString();

        if (productName.equals("")) {
            productNameET.setError("Please enter product name...");
            productNameET.requestFocus();
        } else if (productDescription.equals("")) {
            productDescriptionET.setError("Please enter product description...");
            productDescriptionET.requestFocus();
        } else if (productPrice.equals("")) {
            productPriceET.setError("Please enter product price...");
            productPriceET.requestFocus();
        } else {

            Map<String, Object> productMap = new HashMap<>();

            productMap.put("pid", productId);
            productMap.put("productName", productName);
            productMap.put("productDescription", productDescription);
            productMap.put("productPrice", productPrice);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintainActivity.this, "Change product information successfully.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminMaintainActivity.this, AdminCategoryActivity.class));
                    }

                }
            });


        }

    }

    private void displaySpecificProductInfo() {

        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String productName = snapshot.child("productName").getValue().toString();
                    String productPrice = snapshot.child("productPrice").getValue().toString();
                    String productDescription = snapshot.child("productDescription").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();

                    productNameET.setText(productName);
                    productPriceET.setText(productPrice);
                    productDescriptionET.setText(productDescription);
                    Picasso.get().load(image).into(productImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}