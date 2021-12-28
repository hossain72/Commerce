package com.example.ecommerce.admin;

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

import com.example.ecommerce.R;
import com.example.ecommerce.model.AdminOrder;
import com.example.ecommerce.viewHolder.AdminOrdersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private RecyclerView orderListRV;
    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        orderListRV = findViewById(R.id.orderListRecyclerView);
        orderListRV.setHasFixedSize(true);
        orderListRV.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrder> options =
                new FirebaseRecyclerOptions.Builder<AdminOrder>()
                        .setQuery(orderRef, AdminOrder.class)
                        .build();

        FirebaseRecyclerAdapter<AdminOrder, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrder, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, final int position, @NonNull final AdminOrder model) {

                        holder.orderUsernameTV.setText("Name: " + model.getOrderName());
                        holder.orderPhoneNumberTV.setText("Phone: " + model.getOrderPhoneNUmber());
                        holder.orderTotalPriceTV.setText("Total Price: " + model.getTotalPrice() + " $");
                        holder.orderAddressCityTV.setText("Shipping Address: " + model.getOrderAddress() + ", " + model.getOrderCity());
                        holder.orderDateTimeTV.setText("Order At: " + model.getDate() + " " + model.getTime());

                        holder.showOrderProductBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uId = getRef(position).getKey();
                                Intent intent = new Intent(AdminNewOrderActivity.this, AdminUserProductsActivity.class);
                                intent.putExtra("uid", uId);
                                startActivity(intent);
                            }
                        });

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{
                                        "Yes",
                                        "No"
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrderActivity.this);
                                builder.setTitle("Have you shipped this order products?");

                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (which == 0) {

                                            String uId = getRef(position).getKey();
                                            removeOrder(uId);


                                        } else {
                                            finish();
                                        }

                                    }
                                });

                                builder.show();

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        orderListRV.setAdapter(adapter);
        adapter.startListening();

    }

    private void removeOrder(String uId) {

        orderRef.child(uId).removeValue();

    }


}