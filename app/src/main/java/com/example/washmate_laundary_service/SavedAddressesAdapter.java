package com.example.washmate_laundary_service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.CustomerAddress;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SavedAddressesAdapter extends RecyclerView.Adapter<SavedAddressesAdapter.AddressViewHolder> {

    private final List<CustomerAddress> addresses;
    private final OnAddressActionListener listener;

    public interface OnAddressActionListener {
        void onDelete(CustomerAddress address);
        void onSetDefault(CustomerAddress address);
    }

    public SavedAddressesAdapter(List<CustomerAddress> addresses, OnAddressActionListener listener) {
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        CustomerAddress address = addresses.get(position);
        holder.tvAddressText.setText(address.getAddressText());
        
        StringBuilder details = new StringBuilder();
        if (address.getCity() != null && !address.getCity().isEmpty()) {
            details.append(address.getCity());
        }
        if (address.getPincode() != null && !address.getPincode().isEmpty()) {
            if (details.length() > 0) details.append(", ");
            details.append(address.getPincode());
        }
        
        if (details.length() == 0) {
            holder.tvCityPincode.setVisibility(View.GONE);
        } else {
            holder.tvCityPincode.setVisibility(View.VISIBLE);
            holder.tvCityPincode.setText(details.toString());
        }
        
        holder.tvDefaultBadge.setVisibility(address.getIsDefault() ? View.VISIBLE : View.GONE);
        holder.btnSetDefault.setVisibility(address.getIsDefault() ? View.GONE : View.VISIBLE);

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(address));
        holder.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(address));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressText, tvCityPincode, tvDefaultBadge;
        ImageButton btnDelete;
        MaterialButton btnSetDefault;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressText = itemView.findViewById(R.id.tvAddressText);
            tvCityPincode = itemView.findViewById(R.id.tvCityPincode);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}
