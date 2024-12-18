package com.android.todo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {
    private List<DateItem> dates;
    private Context context;

    public DateAdapter(List<DateItem> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_card, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateItem dateItem = dates.get(position);
        holder.tvMonth.setText(dateItem.getMonth());
        holder.tvDayNumber.setText(dateItem.getDayNumber());
        holder.tvDayName.setText(dateItem.getDayName());
        if (dateItem.isToday()) {
            holder.itemView.setBackgroundColor(Color.parseColor("#00A8E8"));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvDayNumber, tvDayName;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            tvDayName = itemView.findViewById(R.id.tvDayName);
        }
    }
}
