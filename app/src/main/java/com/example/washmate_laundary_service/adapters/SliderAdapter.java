package com.example.washmate_laundary_service.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.washmate_laundary_service.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<SliderItem> sliderItems;

    public SliderAdapter(List<SliderItem> sliderItems) {
        this.sliderItems = sliderItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_slider_image,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        SliderItem item = sliderItems.get(position);
        holder.imageView.setImageResource(item.getImageResId());
        holder.tvTitle.setText(item.getTitle());
        holder.tvSubtitle.setText(item.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvTitle;
        private TextView tvSubtitle;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSlideImage);
            tvTitle = itemView.findViewById(R.id.tvSlideTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSlideSubtitle);
        }
    }
    
    // Simple Item Model
    public static class SliderItem {
        private int imageResId;
        private String title;
        private String subtitle;

        public SliderItem(int imageResId, String title, String subtitle) {
            this.imageResId = imageResId;
            this.title = title;
            this.subtitle = subtitle;
        }

        public int getImageResId() { return imageResId; }
        public String getTitle() { return title; }
        public String getSubtitle() { return subtitle; }
    }
}
