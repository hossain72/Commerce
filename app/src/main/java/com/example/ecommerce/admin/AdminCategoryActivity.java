package com.example.ecommerce.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ecommerce.R;
import com.example.ecommerce.buyers.HomeActivity;
import com.example.ecommerce.buyers.MainActivity;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView tShirtIV, sportShirtIV, femaleDressIV, sweaterIV, glassesIV, pursesBagIV, hatsIV, shoeIV,
            headPhoneIV, laptopIV, watchIV, mobileIV;

    private Button logoutBtn, checkNewOrderBtn, maintainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        tShirtIV = findViewById(R.id.tShirtIV);
        sportShirtIV = findViewById(R.id.sportShirtIV);
        femaleDressIV = findViewById(R.id.femaleDressIV);
        sweaterIV = findViewById(R.id.sweaterIV);
        glassesIV = findViewById(R.id.glassesIV);
        pursesBagIV = findViewById(R.id.pursesBagIV);
        hatsIV = findViewById(R.id.hatsIV);
        shoeIV = findViewById(R.id.shoeIV);
        headPhoneIV = findViewById(R.id.headPhoneIV);
        laptopIV = findViewById(R.id.laptopIV);
        watchIV = findViewById(R.id.watchIV);
        mobileIV = findViewById(R.id.mobileIV);

        logoutBtn = findViewById(R.id.adminLogoutBtn);
        checkNewOrderBtn = findViewById(R.id.checkNewOrderBtn);
        maintainBtn = findViewById(R.id.maintainBtn);

        tShirtIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("tShirt");
            }
        });
        sportShirtIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("SportShirt");
            }
        });
        femaleDressIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("FemaleDress");
            }
        });
        sweaterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Sweater");
            }
        });
        glassesIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Glass");
            }
        });
        pursesBagIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Glass");
            }
        });
        hatsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Hat");
            }
        });
        shoeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Shoe");
            }
        });
        headPhoneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("HeadPhone");
            }
        });
        laptopIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Laptop");
            }
        });
        watchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Watch");
            }
        });
        mobileIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newActivityIntent("Mobile");
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

        checkNewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminCategoryActivity.this, AdminNewOrderActivity.class));
            }
        });

        maintainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);

            }
        });


    }

    private void newActivityIntent(String value) {
        Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
        intent.putExtra("Category", value);
        startActivity(intent);
    }
}