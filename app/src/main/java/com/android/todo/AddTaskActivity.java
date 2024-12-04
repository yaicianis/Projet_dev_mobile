package com.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskName, etTaskDescription, etTaskDate;
    private Spinner categorySpinner;
    private Button btnSaveTask;
    private TextView backBtn;

    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskDate = findViewById(R.id.etTaskDate);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        backBtn = findViewById(R.id.backBtn);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void saveTask() {
        String taskName = etTaskName.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskDate = etTaskDate.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(taskName) || TextUtils.isEmpty(taskDescription) || TextUtils.isEmpty(taskDate)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        CollectionReference tasksRef = firestore.collection("users").document(userId).collection("tasks");

        String taskId = tasksRef.document().getId();
        Task task = new Task(taskId, taskName, taskDescription, taskDate, category, false);

        tasksRef.add(task)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddTaskActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                });
    }
}
