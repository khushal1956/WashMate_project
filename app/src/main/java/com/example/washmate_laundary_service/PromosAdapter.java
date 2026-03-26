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
    private final boolean isAdmin;
    private OnPromoDeleteListener deleteListener;

    public interface OnPromoDeleteListener {
        void onDelete(PromoItem promo);
    }

    public PromosAdapter(List<PromoItem> promos) {
        this(promos, false, null);
    }

    public PromosAdapter(List<PromoItem> promos, boolean isAdmin, OnPromoDeleteListener listener) {
        this.promos = promos;
        this.isAdmin = isAdmin;
        this.deleteListener = listener;
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
        
        String discountInfo = "";
        if (promo.getDiscountValue() > 0) {
            discountInfo = " (" + (int)promo.getDiscountValue() + ("PERCENT".equalsIgnoreCase(promo.getDiscountType()) ? "%" : "₹") + " OFF)";
        }
        holder.tvDesc.setText(promo.getDescription() + discountInfo);

        holder.tvCode.setText(promo.getCode());

        holder.btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Promo Code", promo.getCode());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(v.getContext(), "Code copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnCopy.setVisibility(View.GONE);
            holder.btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) deleteListener.onDelete(promo);
            });
        } else {
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnCopy.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return promos.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvCode;
        MaterialButton btnCopy;
        android.widget.ImageButton btnDelete;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvPromoTitle);
            tvDesc = itemView.findViewById(R.id.tvPromoDesc);
            tvCode = itemView.findViewById(R.id.tvPromoCode);
            btnCopy = itemView.findViewById(R.id.btnCopy);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
