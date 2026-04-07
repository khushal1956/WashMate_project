package com.example.washmate_laundary_service;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.HowItWorksItem;

import java.util.List;

public class HowItWorksAdapter extends RecyclerView.Adapter<HowItWorksAdapter.ViewHolder> {

    private List<HowItWorksItem> itemList;

    public HowItWorksAdapter(List<HowItWorksItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_how_it_works, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HowItWorksItem item = itemList.get(position);
        
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());

        if (item.isVideo()) {
            holder.ivImage.setVisibility(View.GONE);
            holder.vvVideo.setVisibility(View.VISIBLE);
            holder.ivPlayIcon.setVisibility(View.VISIBLE);
            
            Uri videoUri = Uri.parse(item.getVideoUri());
            holder.vvVideo.setVideoURI(videoUri);
            
            // Auto-play on loop
            holder.vvVideo.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0, 0); // Mute for slider
                holder.vvVideo.start();
                holder.ivPlayIcon.setVisibility(View.GONE);
            });
            
        } else {
            holder.vvVideo.setVisibility(View.GONE);
            holder.ivPlayIcon.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.ivImage.setImageResource(item.getImageResId());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivPlayIcon;
        VideoView vvVideo;
        TextView tvTitle, tvDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivStepImage);
            vvVideo = itemView.findViewById(R.id.vvStepVideo);
            ivPlayIcon = itemView.findViewById(R.id.ivPlayIcon);
            tvTitle = itemView.findViewById(R.id.tvStepTitle);
            tvDescription = itemView.findViewById(R.id.tvStepDescription);
        }
    }
}
