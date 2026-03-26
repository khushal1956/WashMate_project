package com.example.washmate_laundary_service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.washmate_laundary_service.models.Customer;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private final List<Customer> customers;

    public CustomerAdapter(List<Customer> customers) {
        this.customers = customers;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvName.setText(customer.getFullName());
        holder.tvEmail.setText(customer.getEmail());
        holder.tvMobile.setText(customer.getMobileNo());

        if (customer.getProfileImageUrl() != null && !customer.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(customer.getProfileImageUrl())
                    .placeholder(R.drawable.ic_person_24dp)
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_person_24dp);
        }

        holder.btnCall.setOnClickListener(v -> {
            String phone = customer.getMobileNo();
            if (phone != null && !phone.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Mobile number not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivProfile;
        TextView tvName, tvEmail, tvMobile;
        ImageButton btnCall;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivCustomerProfile);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvEmail = itemView.findViewById(R.id.tvCustomerEmail);
            tvMobile = itemView.findViewById(R.id.tvCustomerMobile);
            btnCall = itemView.findViewById(R.id.btnCallCustomer);
        }
    }
}
