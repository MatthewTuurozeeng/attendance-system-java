package com.example.attendance_management;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private final String studentId;
    private final String uniqueCode; // Faculty ID to associate the student with a lecturer
    private final BooleanProperty attendance; // Attendance checkbox state
    private final IntegerProperty attendancePercentage; // Attendance percentage

    public Student(String name, String studentId, String email, String uniqueCode) {
        super(name, email);
        this.studentId = studentId;
        this.uniqueCode = uniqueCode;
        this.attendance = new SimpleBooleanProperty(true) {
            @Override
            public String getName() {
                return "attendance";
            }
        };
        this.attendancePercentage = new SimpleIntegerProperty(100) {
            @Override
            public String getName() {
                return "attendancePercentage";
            }
        };

        // Update attendance percentage based on attendance state
        this.attendance.addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                int currentAttendance = attendancePercentage.get();
                attendancePercentage.set(Math.max(0, currentAttendance - 2)); // Ensure it doesn't go below 0%
            }
            saveStudentToCSV(this); // Save changes to CSV
        });
    }

    public String getStudentId() {
        return studentId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public BooleanProperty attendanceProperty() {
        return attendance;
    }

    public IntegerProperty attendancePercentageProperty() {
        return attendancePercentage;
    }

    /**
     * Save or update a student's record in the CSV file.
     */
    public static void saveStudentToCSV(Student student) {
        String path = "students.csv";
        File filename = new File(path);
        List<String> listOfStudentDetails = new ArrayList<>();
        String header = "Name,ID,Email,Unique-Code,Attendance Status,Attendance(in %)";

        // Read existing records or prepare for new file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Ignore empty lines
                    listOfStudentDetails.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. A new file will be created.");
            listOfStudentDetails.add(header); // Add header for a new file
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return; // Exit on failure to read the file
        }

        boolean studentExists = false;

        // Update existing record or add a new one
        for (int i = 1; i < listOfStudentDetails.size(); i++) { // Skip header
            String[] parts = listOfStudentDetails.get(i).split(",");
            if (parts.length >= 2 && parts[1].equals(student.getStudentId())) { // Check index safety
                listOfStudentDetails.set(i, formatStudentRecord(student));
                studentExists = true;
                break;
            }
        }

        if (!studentExists) {
            listOfStudentDetails.add(formatStudentRecord(student));
        }

        // Write back the updated records
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String line : listOfStudentDetails) {
                writer.println(line);
            }
            //System.out.println("Student record saved successfully!");
        } catch (IOException e) {
            System.out.println("Error saving student to CSV: " + e.getMessage());
        }
    }

// Helper method to format a student's record

    /**
     * Load all students from the CSV file.
     */
    public static ObservableList<Student> loadFromCSV() {
        String path = "students.csv";
        File file = new File(path);
        ObservableList<Student> studentList = FXCollections.observableArrayList();

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String header = br.readLine(); // Skip the header line
                String line;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) { // Ensure all fields are present
                        String name = parts[0];
                        String studentId = parts[1];
                        String email = parts[2];
                        String uniqueCode = parts[3];
                        boolean attendance = Boolean.parseBoolean(parts[4]);
                        int attendancePercentage = Integer.parseInt(parts[5]);

                        // Create Student object
                        Student student = new Student(name, studentId, email, uniqueCode);
                        student.attendanceProperty().set(attendance);
                        student.attendancePercentageProperty().set(attendancePercentage);
                        studentList.add(student);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading students from file: " + e.getMessage());
            }
        } else {
            System.out.println("File not found: " + path);
        }

        return studentList;
    }


     //Format a student's details as a CSV record.

    private static String formatStudentRecord(Student student) {
        return String.join(",",
                student.getName(),
                student.getStudentId(),
                student.getEmail(),
                student.getUniqueCode(),
                Boolean.toString(student.attendanceProperty().get()),
                Integer.toString(student.attendancePercentageProperty().get())
        );
    }
}
