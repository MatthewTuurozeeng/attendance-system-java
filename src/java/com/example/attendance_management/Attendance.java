package com.example.attendance_management;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;

import java.util.ArrayList;
import java.util.List;

public class Attendance {
    private final Student student;
    List<Boolean> attendanceRecord=new ArrayList<>();


    public Attendance(Student student) {
        this.student = student;
    }

    // Mark attendance for the student
    public void markAttendance(boolean isPresent) {
        attendanceRecord.add(isPresent);
    }

    // Get the total number of classes
    public int getTotalClasses() {
        return attendanceRecord.size();
    }

    // Count the number of times the student was present
    public int getPresenceCount() {
        return (int) attendanceRecord.stream().filter(p -> p).count();
    }

    // Count the number of times the student was absent
    public int getAbsenceCount() {
        return getTotalClasses() - getPresenceCount();
    }

    // Calculate the attendance percentage
    public double getAttendancePercentage() {
        if (getTotalClasses() == 0) return 0.0;
        return (getPresenceCount() * 100.0) / getTotalClasses();
    }

    // Convert the attendance summary to a readable string
    @Override
    public String toString() {
        return "Attendance Summary for " + student.getName() +
                "\nTotal Classes: " + getTotalClasses() +
                "\nPresent: " + getPresenceCount() +
                "\nAbsent: " + getAbsenceCount() +
                "\nAttendance Percentage: " + String.format("%.2f", getAttendancePercentage()) + "%";
    }

    // Serialize attendance summary to CSV row
    public String toCSVRow() {
        return student.getName() + "," + student.getStudentId() + "," + getAttendancePercentage() + "%";
    }
}
