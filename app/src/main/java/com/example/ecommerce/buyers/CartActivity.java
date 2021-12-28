package com.example.ecommerce.buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.R;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.prevalent.Prevalent;
import com.example.ecommerce.viewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartListRecyclerView;
    private TextView totalPriceTV, msgTV;
    private Button nextBtn;
    private int totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        totalPriceTV = findViewById(R.id.totalPriceTV);
        nextBtn = findViewById(R.id.nextBtn);
        msgTV = findViewById(R.id.msgTV);

        cartListRecyclerView = findViewById(R.id.cartListRecyclerView);
        cartListRecyclerView.setHasFixedSize(true);
        cartListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        totalPriceTV.setText(String.valueOf("Total Price = " + totalPrice + " $"));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(totalPrice));
                startActivity(intent);
                finish();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                        .setQuery(cartListRef.child("User View")
                                .child(Prevalent.currentOnlineUser.getPhoneNumber()).child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                        int productTotalPrice = (Integer.valueOf(model.getProductPrice())) * (Integer.valueOf(model.getQuantity()));

                        holder.cartProductName.setText(model.getProductName());
                        holder.cartProductQuantity.setText("Quantity = " + model.getQuantity());
                        holder.cartProductPrice.setText("Price = " + productTotalPrice + " $");

                        totalPrice = totalPrice + productTotalPrice;
                        totalPriceTV.setText(String.valueOf("Total Price = " + totalPrice + " $"));

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence option[] = new CharSequence[]{
                                        "Edit",
                                        "Remove"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                builder.setTitle("Cart Option : ");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {
                                            Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                            intent.putExtra("pid", model.getPid());
                                            startActivity(intent);
                                        }
                                        if (which == 1) {
                                            cartListRef.child("User View")
                                                    .child(Prevalent.currentOnlineUser.getPhoneNumber())
                                                    .child("Products")
                                                    .child(model.getPid())
                                                    .removeValue()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                            if (task.isSuccessful()) {
                                                                cartListRef.child("Admin View")
                                                                        .child(Prevalent.currentOnlineUser.getPhoneNumber())
                                                                        .child("Products")
                                                                        .child(model.getPid())
                                                                        .removeValue()
                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                                                    intent.putExtra("pid", model.getPid());
                                                                                    startActivity(intent);
                                                                                }
                                                                            }
                                                                        });
                                                            }

                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_list, parent, false);
                        return new CartViewHolder(view);
                    }
                };

        cartListRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    private void checkOrderState() {

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentOnlineUser.getPhoneNumber());

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String orderState = snapshot.child("state").getValue().toString();
                    String userName = snapshot.child("orderName").getValue().toString();

                    if (orderState.equals("order")) {

                        totalPriceTV.setText("Dear " + userName + "\n order is shipped successfully");
                        cartListRecyclerView.setVisibility(View.GONE);
                        msgTV.setVisibility(View.VISIBLE);
                        msgTV.setText("Congratulations, your final order has been shipped successfully. Soon you will receive your order at your door step.");
                        nextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products, once tou received your first order", Toast.LENGTH_SHORT).show();

                    } else if (orderState.equals("not order")) {

                        totalPriceTV.setText("Shipping State = Not Shipped");
                        cartListRecyclerView.setVisibility(View.GONE);
                        msgTV.setVisibility(View.VISIBLE);
                        nextBtn.setVisibility(View.GONE);

                        Toast.makeText(CartActivity.this, "You can purchase more products, once tou received your first order", Toast.LENGTH_SHORT).show();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}