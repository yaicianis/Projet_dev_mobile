package com.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryActivity extends AppCompatActivity {

    private TextView categoryNameTextView;
    private TextView backBtn;
    private FloatingActionButton btnAddTask;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryNameTextView = findViewById(R.id.categoryName);
        backBtn = findViewById(R.id.backBtn);
        btnAddTask = findViewById(R.id.btnAddTask);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();

        String categoryName = getIntent().getStringExtra("CATEGORY_NAME");
        if (categoryName != null) {
            categoryNameTextView.setText(categoryName);
        }

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(taskList);
        recyclerViewTasks.setAdapter(taskAdapter);

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

    private void listenForTasks(String userid, String categoryName) {
        firestore.collection("users").document(userId).collection("tasks")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(CategoryActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Task task = dc.getDocument().toObject(Task.class);
                        switch (dc.getType()) {
                            case ADDED:
                                if (shouldDisplayTask(task, categoryName)) {
                                    taskList.add(task);
                                }
                                break;
                            case MODIFIED:
                                updateTaskInList(task);
                                break;
                            case REMOVED:
                                removeTaskFromList(task);
                                break;
                        }
                    }
                    taskAdapter.notifyDataSetChanged();
                });
    }

    private boolean shouldDisplayTask(Task task, String categoryName) {
        if (categoryName.equals("Daily")) {
            return task.getTaskDate().equals(getCurrentDate());
        } else {
            return categoryName.equals(task.getCategory());
        }
    }

    private void updateTaskInList(Task updatedTask) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getTaskId().equals(updatedTask.getTaskId())) {
                taskList.set(i, updatedTask);
                break;
            }
        }
    }

    private void removeTaskFromList(Task removedTask) {
        taskList.removeIf(task -> task.getTaskId().equals(removedTask.getTaskId()));
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
