package com.android.todo;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private String taskDate;
    private String category;
    private boolean isCompleted;

    // Constructor for Firestore
    public Task(String taskId, String taskName, String taskDescription, String taskDate, String category, boolean isCompleted) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.category = category;
        this.isCompleted = isCompleted;
    }

    public Task() {}

    // Getter methods

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskDate() {
        return taskDate;
    }

    public String getCategory() {
        return category;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setTaskDate(String taskDate) {
        this.taskDate = taskDate;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}