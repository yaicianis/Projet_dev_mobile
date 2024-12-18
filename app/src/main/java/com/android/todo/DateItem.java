package com.android.todo;

public class DateItem {
    private String month;
    private String dayNumber;
    private String dayName;
    private boolean isToday;

    public DateItem(String month, String dayNumber, String dayName, boolean isToday) {
        this.month = month;
        this.dayNumber = dayNumber;
        this.dayName = dayName;
        this.isToday = isToday;
    }

    public String getMonth() { return month; }
    public String getDayNumber() { return dayNumber; }
    public String getDayName() { return dayName; }
    public boolean isToday() { return isToday; }
}