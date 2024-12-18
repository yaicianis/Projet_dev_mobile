package com.android.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskName, etTaskDescription, etTaskDate;
    private Spinner categorySpinner, energySpinner, prioritySpinner;
    private Button btnSaveTask;
    private TextView backBtn;

    private FirebaseFirestore firestore;
    private String userId;
    private String taskId = null;

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
        energySpinner = findViewById(R.id.energySpinner);
        prioritySpinner = findViewById(R.id.prioritySpinner);

        categorySpinner.setSelection(0);
        energySpinner.setSelection(0);
        prioritySpinner.setSelection(0);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        etTaskDate.setOnClickListener(v -> showDatePicker());

        Intent intent = getIntent();
        if (intent.hasExtra("TASK_ID")) {
            // Extract task details from intent
            taskId = intent.getStringExtra("TASK_ID");
            String taskName = intent.getStringExtra("TASK_NAME");
            String taskDescription = intent.getStringExtra("TASK_DESCRIPTION");
            String taskDate = intent.getStringExtra("TASK_DATE");
            String category = intent.getStringExtra("TASK_CATEGORY");
            String energyLevel = intent.getStringExtra("TASK_ENERGY");
            String priorityLevel = intent.getStringExtra("TASK_PRIORITY");

            etTaskName.setText(taskName);
            etTaskDescription.setText(taskDescription);
            etTaskDate.setText(taskDate);
            setSpinnerSelection(categorySpinner, category, R.array.task_categories);
            setSpinnerSelection(energySpinner, energyLevel, R.array.task_energy);
            setSpinnerSelection(prioritySpinner, priorityLevel, R.array.task_priorities);

            btnSaveTask.setText("Modify Task");
        }

        btnSaveTask.setOnClickListener(v -> {
            if (taskId == null) {
                saveNewTaskToFirestore();
            } else {
                modifyExistingTaskInFirestore();
            }
        });

        backBtn.setOnClickListener(v -> {
            Intent intent2 = new Intent(AddTaskActivity.this, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent2);
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value, int arrayResId) {
        String[] array = getResources().getStringArray(arrayResId);
        int index = Arrays.asList(array).indexOf(value);
        if (index >= 0) {
            spinner.setSelection(index);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth ;
                    etTaskDate.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void saveNewTaskToFirestore() {
        String taskName = etTaskName.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskDate = etTaskDate.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String energyLevel = energySpinner.getSelectedItem().toString();
        String priorityLevel = prioritySpinner.getSelectedItem().toString();

        if (validateInputs(taskName, taskDescription, taskDate, category, energyLevel, priorityLevel)) {
            CollectionReference tasksRef = firestore.collection("users").document(userId).collection("tasks");

            String taskId = tasksRef.document().getId();
            Task task = new Task(taskId, taskName, taskDescription, taskDate, category, false, energyLevel, priorityLevel);

            tasksRef.document(taskId).set(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddTaskActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void modifyExistingTaskInFirestore() {
        String taskName = etTaskName.getText().toString().trim();
        String taskDescription = etTaskDescription.getText().toString().trim();
        String taskDate = etTaskDate.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String energyLevel = energySpinner.getSelectedItem().toString();
        String priorityLevel = prioritySpinner.getSelectedItem().toString();

        if (validateInputs(taskName, taskDescription, taskDate, category, energyLevel, priorityLevel)) {
            CollectionReference tasksRef = firestore.collection("users").document(userId).collection("tasks");

            Task updatedTask = new Task(taskId, taskName, taskDescription, taskDate, category, false, energyLevel, priorityLevel);

            tasksRef.document(taskId).set(updatedTask)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AddTaskActivity.this, "Task modified successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddTaskActivity.this, "Failed to modify task", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private boolean validateInputs(String taskName, String taskDescription, String taskDate, String category, String energyLevel, String priorityLevel) {
        if (TextUtils.isEmpty(taskName) || TextUtils.isEmpty(taskDescription) || TextUtils.isEmpty(taskDate)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ("Category".equals(category)) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ("Energy".equals(energyLevel)) {
            Toast.makeText(this, "Please select an energy level", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ("Priority".equals(priorityLevel)) {
            Toast.makeText(this, "Please select a priority level", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
