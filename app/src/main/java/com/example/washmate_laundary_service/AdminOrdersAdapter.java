package com.example.washmate_laundary_service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.Order;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminOrdersAdapter extends RecyclerView.Adapter<AdminOrdersAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAcceptOrder(Order order, int position);
        void onRejectOrder(Order order, int position);
        void onAssignOrder(Order order, int position);
    }


    public AdminOrdersAdapter(OnOrderActionListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    public void updateOrderStatus(int position, String newStatus) {
        if (position >= 0 && position < orders.size()) {
            orders.get(position).setStatus(newStatus);
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, position);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCustomerName, tvOrderId, tvStatus, tvServiceName, tvServiceType,
                tvItemDescription, tvQuantity, tvPickupAddress, tvPickupDate,
                tvPaymentMode, tvTotalAmount;
        private MaterialButton btnAccept, btnReject, btnAssign;
        private LinearLayout llActionButtons, llAssignButton;


        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvItemDescription = itemView.findViewById(R.id.tvItemDescription);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPickupAddress = itemView.findViewById(R.id.tvPickupAddress);
            tvPickupDate = itemView.findViewById(R.id.tvPickupDate);
            tvPaymentMode = itemView.findViewById(R.id.tvPaymentMode);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            llActionButtons = itemView.findViewById(R.id.llActionButtons);
            
            btnAssign = itemView.findViewById(R.id.btnAssign);
            llAssignButton = itemView.findViewById(R.id.llAssignButton);
        }


        public void bind(Order order, int position) {
            tvCustomerName.setText(order.getCustomerName());
            tvOrderId.setText("Order #" + order.getOrderId().substring(0, Math.min(8, order.getOrderId().length())));
            tvStatus.setText(order.getStatus());
            tvServiceName.setText(order.getServiceName());
            tvServiceType.setText(order.getServiceType() != null ? order.getServiceType() : "Standard");
            tvItemDescription.setText(order.getItemDescription());
            tvQuantity.setText(order.getQuantity() + " items");
            
            // Format address
            String address = order.getPickupAddress();
            if (order.getCity() != null) {
                address += ", " + order.getCity();
            }
            tvPickupAddress.setText(address);
            
            tvPickupDate.setText(order.getPickupDate());
            
            // Format payment
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
                    llActionButtons.setVisibility(View.VISIBLE);
                    break;
                case "Accepted":
                    statusColor = 0xFF66BB6A; // Green
                    llActionButtons.setVisibility(View.GONE);
                    break;
                case "Rejected":
                    statusColor = 0xFFEF5350; // Red
                    llActionButtons.setVisibility(View.GONE);
                    break;
                case "Processing":
                    statusColor = 0xFF42A5F5; // Blue
                    llActionButtons.setVisibility(View.GONE);
                    break;
                case "Completed":
                    statusColor = 0xFF66BB6A; // Green
                    llActionButtons.setVisibility(View.GONE);
                    break;
                default:
                    statusColor = 0xFF9E9E9E; // Gray
                    statusColor = 0xFF9E9E9E; // Gray
                    llActionButtons.setVisibility(View.GONE);
            }
            
            // Set status background color preserving shape
            android.graphics.drawable.GradientDrawable background = (android.graphics.drawable.GradientDrawable) tvStatus.getBackground();
            if (background != null) {
                background.setColor(statusColor);
            } else {
                 tvStatus.setBackgroundColor(statusColor);
            }
            
            // Handle Assign Button Visibility
            // Handle Assign Button Visibility
            if ("Accepted".equalsIgnoreCase(order.getStatus()) || 
                "Completed".equalsIgnoreCase(order.getStatus()) || 
                "Picked Up".equalsIgnoreCase(order.getStatus())) {
                if (llAssignButton != null) llAssignButton.setVisibility(View.VISIBLE);
            } else {
                if (llAssignButton != null) llAssignButton.setVisibility(View.GONE);
            }


            
            // Button click listeners
            btnAccept.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAcceptOrder(order, position);
                }
            });
            
            btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectOrder(order, position);
                }
            });
            
            if (btnAssign != null) {
                btnAssign.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onAssignOrder(order, position);
                    }
                });
            }
        }

    }
}
