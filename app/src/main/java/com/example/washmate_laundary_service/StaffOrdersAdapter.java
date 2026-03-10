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

    public interface OnOrderStatusChangeListener {
        void onStatusChange(Order order, String newStatus);
    }

    public void setOnStatusChangeListener(OnOrderStatusChangeListener listener) {
        this.statusChangeListener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull StaffOrderViewHolder holder, int position) {
        Order order = orders.get(position);
        
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvServiceName.setText(order.getServiceName());
        holder.tvServiceType.setText(order.getServiceType());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvTotalItems.setText(order.getQuantity() + " items");
        holder.tvPickupAddress.setText(order.getPickupAddress());
        holder.tvPickupDate.setText(order.getPickupDate());

        // Status Styling and Button Logic
        if ("Processing".equalsIgnoreCase(order.getStatus())) {
            holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_status_pending);
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setText("Mark as Completed");
            holder.btnUpdateStatus.setOnClickListener(v -> {
                if (statusChangeListener != null) {
                    statusChangeListener.onStatusChange(order, "Completed");
                }
            });
        } else if ("Completed".equalsIgnoreCase(order.getStatus())) {
            holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_status_completed);
            holder.btnUpdateStatus.setVisibility(View.GONE); // Already completed
        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
             holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_status_pending);
             holder.btnUpdateStatus.setVisibility(View.VISIBLE);
             holder.btnUpdateStatus.setText("Start Processing");
             holder.btnUpdateStatus.setOnClickListener(v -> {
                 if (statusChangeListener != null) {
                     statusChangeListener.onStatusChange(order, "Processing");
                 }
             });
        } else if ("Out for Delivery".equalsIgnoreCase(order.getStatus())) {
             holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_status_pending); // Or a delivery icon/color
             holder.btnUpdateStatus.setVisibility(View.VISIBLE);
             holder.btnUpdateStatus.setText("Mark as Delivered");
             holder.btnUpdateStatus.setOnClickListener(v -> {
                 if (statusChangeListener != null) {
                     statusChangeListener.onStatusChange(order, "Delivered");
                 }
             });
        } else if ("Delivered".equalsIgnoreCase(order.getStatus())) {
             holder.tvOrderStatus.setBackgroundResource(R.drawable.bg_status_completed);
             holder.btnUpdateStatus.setVisibility(View.GONE);
        } else {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class StaffOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvOrderId, tvServiceName, tvServiceType, tvOrderStatus, tvTotalItems, tvPickupAddress, tvPickupDate;
        android.widget.Button btnUpdateStatus;

        public StaffOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvTotalItems = itemView.findViewById(R.id.tvTotalItems);
            tvPickupAddress = itemView.findViewById(R.id.tvPickupAddress);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
