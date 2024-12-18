package com.android.todo;

public class Task {
    private String taskId;
    private String taskName;
    private String taskDescription;
    private String taskDate;
    private String category;
    private boolean isCompleted;
    private String energyLevel;
    private String priorityLevel;

    public Task(String taskId, String taskName, String taskDescription, String taskDate, String category, boolean isCompleted, String energyLevel, String priorityLevel) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskDate = taskDate;
        this.category = category;
        this.isCompleted = isCompleted;
        this.energyLevel = energyLevel;
        this.priorityLevel = priorityLevel;
    }

    public Task() {}

    // Getter methods

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getTaskDate() { return taskDate; }
    public void setTaskDate(String taskDate) { this.taskDate = taskDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public String getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(String energyLevel) { this.energyLevel = energyLevel; }
    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }
}