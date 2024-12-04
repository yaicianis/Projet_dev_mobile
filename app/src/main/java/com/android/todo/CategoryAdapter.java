package com.android.todo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private Context context;

    public CategoryAdapter(List<Category> categories, Context context) {
        this.categories = categories;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getName());
        holder.categoryIcon.setImageResource(category.getIconResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryActivity.class);
            intent.putExtra("CATEGORY_NAME", category.getName());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
