package com.android.todo;

import com.android.todo.Task;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskName.setText(task.getTaskName());
        holder.taskDescription.setText(task.getTaskDescription());
        holder.time.setText(task.getTaskDate());
        holder.checkBox.setChecked(task.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDescription, time;
        CheckBox checkBox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            time = itemView.findViewById(R.id.time);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
