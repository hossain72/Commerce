package com.example.ecommerce.viewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.Interface.ItemClickListener;
import com.example.ecommerce.R;

public class AdminOrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView orderUsernameTV, orderPhoneNumberTV, orderTotalPriceTV, orderAddressCityTV, orderDateTimeTV;
    public Button showOrderProductBtn;
    private ItemClickListener itemClickListener;

    public AdminOrdersViewHolder(@NonNull View itemView) {
        super(itemView);

        orderUsernameTV = itemView.findViewById(R.id.orderUserNameTV);
        orderPhoneNumberTV = itemView.findViewById(R.id.orderPhoneNumberTV);
        orderTotalPriceTV = itemView.findViewById(R.id.orderTotalPriceTV);
        orderAddressCityTV = itemView.findViewById(R.id.orderAddressCityTV);
        orderDateTimeTV = itemView.findViewById(R.id.orderDateTimeTV);
        showOrderProductBtn = itemView.findViewById(R.id.showOrderProductBtn);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);

    }
}
