package com.example.washmate_laundary_service.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.R;
import com.example.washmate_laundary_service.models.ServiceItem;

import java.util.List;

public class ServiceSelectionAdapter extends RecyclerView.Adapter<ServiceSelectionAdapter.ViewHolder> {

    private List<ServiceItem> serviceList;
    private OnQuantityChangeListener listener;

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public ServiceSelectionAdapter(List<ServiceItem> serviceList, OnQuantityChangeListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ServiceItem item = serviceList.get(position);

        holder.tvServiceName.setText(item.getName());
        updatePriceText(holder, item);
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            updatePriceText(holder, item);
            if (listener != null) {
                listener.onQuantityChanged();
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                updatePriceText(holder, item);
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            }
        });
    }

    private void updatePriceText(ViewHolder holder, ServiceItem item) {
        if (item.getQuantity() > 0) {
            double total = item.getPrice() * item.getQuantity();
            holder.tvServicePrice.setText("₹" + (int) item.getPrice() + " x " + item.getQuantity() + " = ₹" + (int) total);
            holder.tvServicePrice.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.washmatePrimary));
        } else {
            holder.tvServicePrice.setText("₹" + (int) item.getPrice() + "/item");
            holder.tvServicePrice.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName, tvServicePrice, tvQuantity;
        ImageButton btnDecrease, btnIncrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServicePrice = itemView.findViewById(R.id.tvServicePrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}
