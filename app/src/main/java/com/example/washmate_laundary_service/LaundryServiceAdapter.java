package com.example.washmate_laundary_service;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.LaundryService;
import java.util.List;
import java.util.Locale;

public class LaundryServiceAdapter extends RecyclerView.Adapter<LaundryServiceAdapter.ServiceViewHolder> {

    private List<LaundryService> serviceList;
    private OnServiceClickListener clickListener;

    public interface OnServiceClickListener {
        void onServiceClick(LaundryService service);
    }

    public LaundryServiceAdapter(List<LaundryService> serviceList, OnServiceClickListener clickListener) {
        this.serviceList = serviceList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_laundry_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        LaundryService service = serviceList.get(position);
        holder.tvServiceName.setText(service.getServiceName());
        holder.tvServicePrice.setText(String.format(Locale.getDefault(), "₹ %.2f", service.getPrice()));
        holder.tvServiceStatus.setText(service.getStatus());

        // Dynamic Status Styling
        if ("Active".equalsIgnoreCase(service.getStatus())) {
            holder.tvServiceStatus.setBackgroundColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.tvServiceStatus.setBackgroundColor(Color.parseColor("#F44336")); // Red
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null && "Active".equalsIgnoreCase(service.getStatus())) {
                clickListener.onServiceClick(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList == null ? 0 : serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServicePrice, tvServiceStatus;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvServiceStatus = itemView.findViewById(R.id.tvServiceStatus);
        }
    }
}
