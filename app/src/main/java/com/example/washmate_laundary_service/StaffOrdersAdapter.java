package com.example.washmate_laundary_service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.Order;
import java.util.List;
import java.util.ArrayList;

public class StaffOrdersAdapter extends RecyclerView.Adapter<StaffOrdersAdapter.StaffOrderViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StaffOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff_order, parent, false);
        return new StaffOrderViewHolder(view);
    }

    private OnOrderStatusChangeListener statusChangeListener;
    private OnOrderMessageListener messageListener;

    public interface OnOrderStatusChangeListener {
        void onStatusChange(Order order, String newStatus);
    }
    
    public interface OnOrderMessageListener {
        void onMessageClick(Order order);
    }

    public void setOnStatusChangeListener(OnOrderStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }
    
    public void setOnOrderMessageListener(OnOrderMessageListener listener) {
        this.messageListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull StaffOrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        String name = order.getCustomerName() != null ? order.getCustomerName() : "Customer";
        holder.tvCustomerName.setText(name);
        
        if (name.length() > 0) {
            holder.tvCustomerInitials.setText(name.substring(0, 1).toUpperCase());
        } else {
            holder.tvCustomerInitials.setText("C");
        }
        
        holder.tvOrderId.setText("ORDER #" + order.getOrderId().toUpperCase());
        holder.tvServiceName.setText(order.getServiceName());
        holder.tvServiceType.setText(order.getServiceType());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvTotalItems.setText(order.getQuantity() + " items total");
        holder.tvPickupAddress.setText(order.getPickupAddress());
        holder.tvPickupDate.setText(order.getPickupDate());

        // Status Styling & Deep Color Tints
        String status = order.getStatus() != null ? order.getStatus() : "Pending";
        
        if ("Processing".equalsIgnoreCase(status)) {
            holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#3B82F6"))); // Blue
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setText("Mark as Completed");
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (statusChangeListener != null) statusChangeListener.onStatusChange(order, "Completed");
            });
        } else if ("Completed".equalsIgnoreCase(status)) {
            holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#10B981"))); // Green
            holder.btnUpdateStatus.setVisibility(View.GONE); 
        } else if ("Pending".equalsIgnoreCase(status)) {
             holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EAB308"))); // Amber
             holder.btnUpdateStatus.setVisibility(View.VISIBLE);
             holder.btnUpdateStatus.setText("Start Processing");
             holder.btnUpdateStatus.setOnClickListener(v -> {
                 if (statusChangeListener != null) statusChangeListener.onStatusChange(order, "Processing");
             });
        } else if ("Out for Delivery".equalsIgnoreCase(status)) {
             holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#8B5CF6"))); // Purple
             holder.btnUpdateStatus.setVisibility(View.VISIBLE);
             holder.btnUpdateStatus.setText("Mark as Delivered");
             holder.btnUpdateStatus.setOnClickListener(v -> {
                 if (statusChangeListener != null) statusChangeListener.onStatusChange(order, "Delivered");
             });
        } else if ("Delivered".equalsIgnoreCase(status)) {
             holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#10B981"))); // Green
             holder.btnUpdateStatus.setVisibility(View.GONE);
        } else {
             holder.tvOrderStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EF4444"))); // Red fallback
             holder.btnUpdateStatus.setVisibility(View.GONE);
        }
        
        // Show btnMessageAdmin
        holder.btnMessageAdmin.setOnClickListener(v -> {
            if(messageListener != null) messageListener.onMessageClick(order);
        });
        
        // Unhide messages if present
        if (order.getAdminNotes() != null && !order.getAdminNotes().trim().isEmpty()) {
            holder.btnMessageAdmin.setText("Admin Reply: " + order.getAdminNotes());
            holder.btnMessageAdmin.setTextColor(android.graphics.Color.parseColor("#34D399"));
        } else {
            holder.btnMessageAdmin.setText("Report / Message Admin");
            holder.btnMessageAdmin.setTextColor(android.graphics.Color.parseColor("#94A3B8"));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class StaffOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvCustomerInitials, tvOrderId, tvServiceName, tvServiceType, tvOrderStatus, tvTotalItems, tvPickupAddress, tvPickupDate;
        android.widget.Button btnUpdateStatus, btnMessageAdmin;

        public StaffOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvCustomerInitials = itemView.findViewById(R.id.tvCustomerInitials);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            tvPickupAddress = itemView.findViewById(R.id.tvPickupAddress);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
            btnMessageAdmin = itemView.findViewById(R.id.btnMessageAdmin);
        }
    }
}
