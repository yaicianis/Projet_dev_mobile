package com.android.todo;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private String userEnergyLevel = "medium";

    public TaskAdapter(List<Task> taskList, String userEnergyLevel) {
        this.taskList = taskList;
        this.userEnergyLevel = userEnergyLevel;
    }

    public void sortTasksByPriorityAndEnergy() {
        Log.d("TaskWeight", "Energy level (sort tasks): " + userEnergyLevel);

        taskList.sort((task1, task2) -> {
            int task1Weight = -(getEnergyDifference(task1.getEnergyLevel()) + getPriorityWeight(task1.getPriorityLevel()));
            int task2Weight = -(getEnergyDifference(task2.getEnergyLevel()) + getPriorityWeight(task2.getPriorityLevel()));

            Log.d("TaskWeight", task1.getTaskName() + " Weight: " + task1Weight);
            Log.d("TaskWeight", task2.getTaskName() + " Weight: " + task2Weight);

            return Integer.compare(task1Weight, task2Weight);
        });
        notifyDataSetChanged();
    }

    private int getPriorityWeight(String priority) {
        Log.d("Priority", "Task Priority: " + priority);
        switch (priority) {
            case "Very High": return 4;
            case "High": return 3;
            case "Medium": return 2;
            case "Low": return 1;
            default: return 0;
        }
    }

    private int getEnergyDifference(String taskEnergy) {
        return calculateEnergyWeight(taskEnergy, userEnergyLevel);
    }

    private int calculateEnergyWeight(String taskEnergy, String userEnergy) {
        if ((taskEnergy.equals("High") && userEnergy.equals("high")) ||
                (taskEnergy.equals("Medium") && userEnergy.equals("medium")) ||
                (taskEnergy.equals("Low") && userEnergy.equals("low"))) {
            return 3;
        } else if ((taskEnergy.equals("High") && userEnergy.equals("medium")) ||
                (taskEnergy.equals("Medium") && userEnergy.equals("high")) ||
                (taskEnergy.equals("Medium") && userEnergy.equals("low")) ||
                (taskEnergy.equals("Low") && userEnergy.equals("medium"))) {
            return 2;
        } else if ((taskEnergy.equals("High") && userEnergy.equals("low")) ||
                (taskEnergy.equals("Low") && userEnergy.equals("high"))) {
            return 1;
        } else {
            return 0;
        }
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
        holder.taskTime.setText(task.getTaskDate());
        holder.energy.setText("Energy: " + task.getEnergyLevel());
        holder.priority.setText("Priority: " + task.getPriorityLevel());
        holder.taskTime.setText(task.getTaskDate());
        holder.checkBox.setChecked(task.isCompleted());

        if (!task.isCompleted() && isTaskOverdue(task.getTaskDate())) {
            holder.taskTime.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        } else {
            holder.taskTime.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }


        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            updateTaskInFirebase(task);

            if (isChecked) {
                taskList.remove(position);
                taskList.add(taskList.size(), task);
                notifyItemMoved(position, taskList.size() - 1);
            }

            sortTasksByPriorityAndEnergy();
        });

        holder.optionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(holder.optionsButton.getContext(), holder.optionsButton);
            popupMenu.inflate(R.menu.task_options_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                if(item.getItemId() == R.id.modify_task) {
                    Intent intent = new Intent(holder.optionsButton.getContext(), AddTaskActivity.class);
                    intent.putExtra("TASK_ID", task.getTaskId());
                    intent.putExtra("TASK_NAME", task.getTaskName());
                    intent.putExtra("TASK_DESCRIPTION", task.getTaskDescription());
                    intent.putExtra("TASK_DATE", task.getTaskDate());
                    intent.putExtra("TASK_ENERGY", task.getEnergyLevel());
                    intent.putExtra("TASK_PRIORITY", task.getPriorityLevel());
                    intent.putExtra("TASK_CATEGORY", task.getCategory());
                    holder.optionsButton.getContext().startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.delete_task) {
                    deleteTaskFromFirebase(task, position);
                    return true;
                }
                else return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private boolean isTaskOverdue(String taskDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date dueDate = sdf.parse(taskDate);
            String currentDateStr = getCurrentDate();
            Date currentDate = sdf.parse(currentDateStr);

            return dueDate != null && !dueDate.equals(currentDate) && dueDate.before(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void deleteTaskFromFirebase(Task task, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users").document(userId).collection("tasks")
                .document(task.getTaskId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (position >= 0 && position < taskList.size()) {
                        taskList.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> {});
    }

    private void updateTaskInFirebase(Task task) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance()
                .collection("users").document(userId).collection("tasks")
                .document(task.getTaskId())
                .set(task)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(e -> {});
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskDescription, taskTime, energy, priority, optionsButton;
        CheckBox checkBox;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            taskTime = itemView.findViewById(R.id.time);
            priority = itemView.findViewById(R.id.priority);
            energy = itemView.findViewById(R.id.energy);
            checkBox = itemView.findViewById(R.id.checkBox);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }
}
