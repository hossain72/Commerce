package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce.R;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImageIV;
    private TextView productNameTV, productDescriptionTV, productPriceTV;
    private ElegantNumberButton numberButton;
    private Button addProductCartBtn;
    private String productId = "", state = "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productImageIV = findViewById(R.id.productImageDetailsIV);
        productNameTV = findViewById(R.id.productNameDetailsTV);
        productDescriptionTV = findViewById(R.id.productDescriptionDetailsTV);
        productPriceTV = findViewById(R.id.productPriceDetailsTV);
        numberButton = findViewById(R.id.elegantNumberButton);
        addProductCartBtn = findViewById(R.id.addToCartBtn);

        productId = getIntent().getStringExtra("pid");

        getProductDetails(productId);


        addProductCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (state.equals("Order Placed") || state.equals("Order Shipped")) {
                    Toast.makeText(ProductDetailsActivity.this, "You can purchase more products, once your order is shipped or confirmed", Toast.LENGTH_LONG).show();
                } else {
                    addingToCartList();
                }

            }
        });


    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendarForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productId);
        cartMap.put("productName", productNameTV.getText().toString());
        cartMap.put("productPrice", productPriceTV.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", numberButton.getNumber());
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhoneNumber())
                .child("Products").child(productId).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhoneNumber())
                                    .child("Products").child(productId).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductDetailsActivity.this, "Added to Cart List", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(ProductDetailsActivity.this, HomeActivity.class));

                                            }

                                        }
                                    });

                        }

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void getProductDetails(String productId) {

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);

                    productNameTV.setText(product.getProductName());
                    productDescriptionTV.setText(product.getProductDescription());
                    productPriceTV.setText(product.getProductPrice());
                    Picasso.get().load(product.getImage()).into(productImageIV);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkOrderState() {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNumber());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String orderState = snapshot.child("state").getValue().toString();

                    if (orderState.equals("order")) {

                        state = "Order Shipped";

                    } else if (orderState.equals("not order")) {

                        state = "Order Placed";

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}