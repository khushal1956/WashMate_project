package com.example.washmate_laundary_service;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.Order;
import java.util.List;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.TrackViewHolder> {

    private List<Order> activeOrders;
    private Context context;

    public TrackOrderAdapter(Context context, List<Order> activeOrders) {
        this.context = context;
        this.activeOrders = activeOrders;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_track_order_compact, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        Order order = activeOrders.get(position);
        holder.tvServiceName.setText(order.getServiceName());
        holder.tvOrderId.setText("#" + order.getOrderId());
        holder.tvStatus.setText(order.getStatus());
        
        // Simple color logic based on status
        String status = order.getStatus().toLowerCase();
        int statusColor = 0xFF0EA5E9; // Default blue
        if (status.contains("pending")) statusColor = 0xFFF59E0B; // Orange
        else if (status.contains("process") || status.contains("clean")) statusColor = 0xFF8B5CF6; // Purple
        else if (status.contains("out")) statusColor = 0xFF10B981; // Green
        
        holder.tvStatus.setTextColor(statusColor);
        holder.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));
        holder.layoutStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf((statusColor & 0x00FFFFFF) | 0x20000000));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            context.startActivity(intent);
        });
        
        holder.btnTrack.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return activeOrders.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvOrderId, tvStatus;
        View viewStatusDot, layoutStatus, btnTrack;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
            btnTrack = itemView.findViewById(R.id.btnTrack);
        }
    }
}
