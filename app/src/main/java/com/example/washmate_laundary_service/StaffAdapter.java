package com.example.washmate_laundary_service;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.washmate_laundary_service.models.Staff;
import java.util.List;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private List<Staff> staffList;
    private OnStaffActionListener listener;

    public interface OnStaffActionListener {
        void onDeleteStaff(Staff staff);
    }

    public StaffAdapter(List<Staff> staffList, OnStaffActionListener listener) {
        this.staffList = staffList;
        this.listener = listener;
    }

    // Constructor for backward compatibility if needed, though better to use the one with listener
    public StaffAdapter(List<Staff> staffList) {
        this.staffList = staffList;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.tvStaffName.setText(staff.getFullName());
        holder.tvStaffRole.setText(staff.getStaffRole());
        holder.tvStaffPhone.setText(staff.getMobileNo());
        holder.tvAvailability.setText(staff.getAvailabilityStatus());

        // Dynamic Styling for Status
        if ("Available".equalsIgnoreCase(staff.getAvailabilityStatus())) {
            holder.tvAvailability.setTextColor(holder.itemView.getContext().getColor(R.color.statusCompleted));
        } else {
            holder.tvAvailability.setTextColor(holder.itemView.getContext().getColor(R.color.textColorSecondary));
        }

        holder.btnDeleteStaff.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteStaff(staff);
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffList == null ? 0 : staffList.size();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView tvStaffName, tvStaffRole, tvStaffPhone, tvAvailability;
        ImageButton btnDeleteStaff;

        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            tvStaffRole = itemView.findViewById(R.id.tvStaffRole);
            tvStaffPhone = itemView.findViewById(R.id.tvStaffPhone);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            btnDeleteStaff = itemView.findViewById(R.id.btnDeleteStaff);
        }
    }
}
