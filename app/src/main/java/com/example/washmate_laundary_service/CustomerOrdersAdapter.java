package com.example.washmate_laundary_service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.Order;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrdersAdapter extends RecyclerView.Adapter<CustomerOrdersAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_customer_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvServiceName, tvOrderId, tvStatus, tvItemDescription,
                tvQuantity, tvPickupDate, tvPaymentMode, tvTotalAmount;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
        }

        public void bind(Order order) {
            tvServiceName.setText(order.getServiceName());
            tvOrderId.setText("Order #" + order.getOrderId().substring(0, 8));
            tvStatus.setText(order.getStatus());
            tvItemDescription.setText(order.getItemDescription());
            tvQuantity.setText(order.getQuantity() + " items");
            tvPickupDate.setText(order.getPickupDate());
            
            // Format payment mode
            String paymentText = order.getPaymentMode();
            if ("Online".equals(order.getPaymentMode()) && order.getPaymentMethod() != null) {
                paymentText += " - " + order.getPaymentMethod();
            }
            tvPaymentMode.setText(paymentText);
            
            tvTotalAmount.setText("₹" + String.format("%.0f", order.getTotalAmount()));
            
            // Set status background color
            int statusColor;
            switch (order.getStatus()) {
                case "Pending":
                    statusColor = 0xFFFFA726; // Orange
                    break;
                case "Processing":
                    statusColor = 0xFF42A5F5; // Blue
                    break;
                case "Completed":
                    statusColor = 0xFF66BB6A; // Green
                    break;
                case "Cancelled":
                    statusColor = 0xFFEF5350; // Red
                    break;
                default:
                    statusColor = 0xFF9E9E9E; // Gray
            }
            tvStatus.setBackgroundColor(statusColor);

            // Click listener to track order
            itemView.setOnClickListener(v -> {
                android.content.Context context = itemView.getContext();
                Intent intent = new Intent(context, OrderTrackingActivity.class);
                intent.putExtra("ORDER_ID", order.getOrderId());
                context.startActivity(intent);
            });
        }
    }
}
