package com.android.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {

    private TextView categoryNameTextView, backBtn, emptyStateView;
    private FloatingActionButton btnAddTask;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;
    private String userEnergyLevel = "medium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryNameTextView = findViewById(R.id.categoryName);
        backBtn = findViewById(R.id.backBtn);
        btnAddTask = findViewById(R.id.btnAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        emptyStateView = findViewById(R.id.emptyStateView);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            categoryNameTextView.setText(categoryName);
        }

        userEnergyLevel = getIntent().getStringExtra("USER_ENERGY");
        if (userEnergyLevel == null) {
            userEnergyLevel = "medium";
        }

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList, userEnergyLevel);
        recyclerViewTasks.setAdapter(taskAdapter);

        setUserEnergyLevel(userEnergyLevel);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        listenForTasks(userId, categoryName);
    }

    private void listenForTasks(String userId, String categoryName) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firestore.collection("users").document(userId).collection("tasks")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            return;
                        }

                        boolean taskListChanged = false;

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Task task = dc.getDocument().toObject(Task.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    if (shouldDisplayTask(task, categoryName)) {
                                        taskList.add(task);
                                        taskListChanged = true;
                                    }
                                    break;
                                case MODIFIED:
                                    if (updateTaskInList(task)) {
                                        taskListChanged = true;
                                    }
                                    break;
                                case REMOVED:
                                    if (removeTaskFromList(task)) {
                                        taskListChanged = true;
                                    }
                                    break;
                            }
                        }


                        // Show or hide the empty state view
                        emptyStateView.setVisibility(taskList.isEmpty() ? View.VISIBLE : View.GONE);

                        // Apply the energy level filter and update the adapter
                        if (taskListChanged) {
                            setUserEnergyLevel(userEnergyLevel);
                        }
                    });
        } else {
            Log.d("TaskLoading", "User is not logged in, skipping task load.");
        }
    }


    private boolean shouldDisplayTask(Task task, String categoryName) {
        if (categoryName.equals("Daily")) {
            return task.getTaskDate().equals(getCurrentDate());
        } else if (!categoryName.equals(task.getCategory())) {
            return false;
        }
        return true;
    }

    private boolean isEnergyLevelCompatible(Task task) {
        String taskEnergyLevel = task.getEnergyLevel();
        return taskEnergyLevel == null || taskEnergyLevel.equals(userEnergyLevel) || userEnergyLevel.equals("medium");
    }

    private boolean updateTaskInList(Task updatedTask) {
        if (updatedTask == null || updatedTask.getTaskId() == null) {
            Log.e("UpdateTask", "Invalid task passed for update.");
            return false;
        }

        for (int i = 0; i < taskList.size(); i++) {
            if (updatedTask.getTaskId().equals(taskList.get(i).getTaskId())) {
                taskList.set(i, updatedTask);
                taskAdapter.notifyItemChanged(i);
                Log.d("UpdateTask", "Task updated: " + updatedTask.getTaskName());
                return true;
            }
        }

        Log.w("UpdateTask", "Task not found in list: " + updatedTask.getTaskId());
        return false;
    }

    private boolean removeTaskFromList(Task removedTask) {
        if (removedTask == null || removedTask.getTaskId() == null) {
            Log.e("RemoveTask", "Invalid task passed for removal.");
            return false;
        }

        for (int i = 0; i < taskList.size(); i++) {
            if (removedTask.getTaskId().equals(taskList.get(i).getTaskId())) {
                taskList.remove(i);
                taskAdapter.notifyItemRemoved(i);
                Log.d("RemoveTask", "Task removed: " + removedTask.getTaskName());
                return true;
            }
        }

        Log.w("RemoveTask", "Task not found in list: " + removedTask.getTaskId());
        return false;
    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void setUserEnergyLevel(String energyLevel) {
        this.userEnergyLevel = energyLevel;

        if (taskAdapter != null) {

            taskAdapter.sortTasksByPriorityAndEnergy();
            taskAdapter.notifyDataSetChanged();
        }
    }

}
