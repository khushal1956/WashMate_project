package com.example.washmate_laundary_service;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.models.PromoItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PromosAdapter extends RecyclerView.Adapter<PromosAdapter.PromoViewHolder> {

    private final List<PromoItem> promos;

    public PromosAdapter(List<PromoItem> promos) {
        this.promos = promos;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promo, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        PromoItem promo = promos.get(position);
        holder.tvTitle.setText(promo.getTitle());
        holder.tvDesc.setText(promo.getDescription());
        holder.tvCode.setText(promo.getCode());

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Promo Code", promo.getCode());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(v.getContext(), "Code copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return promos.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvCode;
        MaterialButton btnCopy;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPromoTitle);
            tvDesc = itemView.findViewById(R.id.tvPromoDesc);
            tvCode = itemView.findViewById(R.id.tvPromoCode);
            btnCopy = itemView.findViewById(R.id.btnCopy);
        }
    }
}
