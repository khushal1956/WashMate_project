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
        
        // Premium color logic based on status
        String status = order.getStatus().toLowerCase();
        int statusColor = 0xFF0EA5E9; // Sky Blue (Processing)
        int segmentsCompleted = 1;

        if (status.contains("pending")) {
            statusColor = 0xFFF59E0B; // Amber
            segmentsCompleted = 1;
        } else if (status.contains("rejected")) {
            statusColor = 0xFFEF4444; // Bright Red
            segmentsCompleted = 0;
        } else if (status.contains("process") || status.contains("wash") || status.contains("iron")) {
            statusColor = 0xFF8B5CF6; // Violet
            segmentsCompleted = 2;
        } else if (status.contains("ready") || status.contains("clean")) {
            statusColor = 0xFF06B6D4; // Cyan
            segmentsCompleted = 3;
        } else if (status.contains("out") || status.contains("delivery")) {
            statusColor = 0xFF10B981; // Emerald
            segmentsCompleted = 4;
        }
        
        holder.tvStatus.setTextColor(statusColor);
        holder.viewStatusDot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(statusColor));
        holder.layoutStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf((statusColor & 0x00FFFFFF) | 0x15000000));

        // Update Progress Segments
        updateProgressSegments(holder, segmentsCompleted, statusColor);

        // Date Handling (Simple logic for delivery estimation)
        if (order.getPickupDate() != null) {
            holder.tvEstimate.setText("Delivery: " + order.getPickupDate());
        }

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

        holder.btnSupport.setOnClickListener(v -> {
            Intent intent = new Intent(context, SupportActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            context.startActivity(intent);
        });
    }

    private void updateProgressSegments(TrackViewHolder holder, int completed, int color) {
        View[] segments = {holder.s1, holder.s2, holder.s3, holder.s4};
        for (int i = 0; i < segments.length; i++) {
            if (i < completed) {
                segments[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
                segments[i].setAlpha(1.0f);
            } else {
                segments[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(0x20FFFFFF));
                segments[i].setAlpha(1.0f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return activeOrders.size();
    }

    public static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvOrderId, tvStatus, tvEstimate;
        View viewStatusDot, layoutStatus, btnTrack, btnSupport;
        View s1, s2, s3, s4;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvEstimate = itemView.findViewById(R.id.tvEstimate);
            viewStatusDot = itemView.findViewById(R.id.viewStatusDot);
            layoutStatus = itemView.findViewById(R.id.layoutStatus);
            btnTrack = itemView.findViewById(R.id.btnTrack);
            btnSupport = itemView.findViewById(R.id.btnSupport);
            s1 = itemView.findViewById(R.id.segment1);
            s2 = itemView.findViewById(R.id.segment2);
            s3 = itemView.findViewById(R.id.segment3);
            s4 = itemView.findViewById(R.id.segment4);
        }
    }
}
